package com.ipintelligence.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ipintelligence.dto.IpAssetDto;
import com.ipintelligence.dto.SearchRequestDto;
import com.ipintelligence.dto.SearchResultDto;
import com.ipintelligence.model.IpAsset;
import com.ipintelligence.model.SearchHistory;
import com.ipintelligence.model.User;
import com.ipintelligence.repo.IpAssetRepository;
import com.ipintelligence.repo.SearchHistoryRepository;
import com.ipintelligence.service.IpSearchService;
import com.ipintelligence.service.api.PatentOfficeApiClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class IpSearchServiceImpl implements IpSearchService {

    private final List<PatentOfficeApiClient> apiClients;
    private final IpAssetRepository ipAssetRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final ObjectMapper objectMapper;

    public IpSearchServiceImpl(List<PatentOfficeApiClient> apiClients,
                               IpAssetRepository ipAssetRepository,
                               SearchHistoryRepository searchHistoryRepository,
                               ObjectMapper objectMapper) {
        this.apiClients = apiClients;
        this.ipAssetRepository = ipAssetRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public SearchResultDto searchAcrossAllSources(SearchRequestDto searchRequest, User user) {
        List<CompletableFuture<SearchResultDto>> futures = apiClients.stream()
                .filter(PatentOfficeApiClient::isAvailable)
                .map(client -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return client.search(searchRequest);
                    } catch (Exception e) {
                        log.error("Error searching with client: {}", client.getDataSource(), e);
                        return createEmptyResult(searchRequest, client.getDataSource());
                    }
                }))
                .collect(Collectors.toList());

        // Combine results from all sources
        SearchResultDto combinedResult = combineSearchResults(futures, searchRequest);

        // Save search history
        saveSearchHistory(searchRequest, combinedResult, user);

        // Save new assets to database
        if (!combinedResult.getAssets().isEmpty()) {
            saveSearchResults(combinedResult.getAssets());
        }

        return combinedResult;
    }

    @Override
    public SearchResultDto searchSpecificSource(SearchRequestDto searchRequest, String dataSource, User user) {
        PatentOfficeApiClient client = apiClients.stream()
                .filter(c -> c.getDataSource().equals(dataSource))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Data source not found: " + dataSource));

        if (!client.isAvailable()) {
            throw new RuntimeException("Data source is not available: " + dataSource);
        }

        SearchResultDto result = client.search(searchRequest);

        // Save search history
        saveSearchHistory(searchRequest, result, user);

        // Save new assets to database
        if (!result.getAssets().isEmpty()) {
            saveSearchResults(result.getAssets());
        }

        return result;
    }

    @Override
    public Page<IpAsset> searchLocalDatabase(SearchRequestDto searchRequest, Pageable pageable) {
        if (searchRequest.getQuery() != null && !searchRequest.getQuery().isEmpty()) {
            return ipAssetRepository.searchByKeyword(searchRequest.getQuery(), pageable);
        }

        return ipAssetRepository.searchWithFilters(
                searchRequest.getTitle(),
                searchRequest.getInventor(),
                searchRequest.getAssignee(),
                searchRequest.getJurisdiction(),
                searchRequest.getAssetType(),
                searchRequest.getPatentOffice(),
                searchRequest.getKeywords(),
                searchRequest.getFromDate(),
                searchRequest.getToDate(),
                pageable
        );
    }

    @Override
    public IpAssetDto getAssetDetails(Long assetId) {
        Optional<IpAsset> asset = ipAssetRepository.findById(assetId);
        return asset.map(this::convertToDto).orElse(null);
    }

    @Override
    public IpAssetDto getAssetDetailsByExternalId(String externalId, String dataSource) {
        // First try to find in local database
        Optional<IpAsset> localAsset = ipAssetRepository.findByExternalIdAndPatentOffice(externalId, dataSource);
        if (localAsset.isPresent()) {
            return convertToDto(localAsset.get());
        }

        // If not found locally, try to fetch from API
        PatentOfficeApiClient client = apiClients.stream()
                .filter(c -> c.getDataSource().equals(dataSource))
                .findFirst()
                .orElse(null);

        if (client != null && client.isAvailable()) {
            IpAssetDto assetDto = client.getAssetDetails(externalId);
            if (assetDto != null) {
                // Save to database for future reference
                saveSearchResults(Collections.singletonList(assetDto));
                return assetDto;
            }
        }

        return null;
    }

    @Override
    public List<IpAssetDto> saveSearchResults(List<IpAssetDto> assets) {
        List<IpAsset> savedAssets = new ArrayList<>();

        for (IpAssetDto assetDto : assets) {
            // Check if asset already exists
            Optional<IpAsset> existing = ipAssetRepository.findByExternalIdAndPatentOffice(
                    assetDto.getExternalId(), assetDto.getPatentOffice());

            IpAsset asset;
            if (existing.isPresent()) {
                asset = existing.get();
                // Update existing asset with new data
                updateAssetFromDto(asset, assetDto);
            } else {
                asset = convertToEntity(assetDto);
            }

            savedAssets.add(ipAssetRepository.save(asset));
        }

        return savedAssets.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<SearchHistory> getUserSearchHistory(User user, Pageable pageable) {
        Page<SearchHistory> historyPage = searchHistoryRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        return historyPage.getContent();
    }

    @Override
    public void saveSearchHistory(SearchRequestDto searchRequest, SearchResultDto result, User user) {
        try {
            SearchHistory history = new SearchHistory();
            history.setUser(user);
            history.setSearchQuery(searchRequest.getQuery());
            history.setSearchFilters(objectMapper.writeValueAsString(searchRequest));
            history.setResultsCount((int) result.getTotalElements());
            history.setDataSource(result.getDataSource());
            history.setSearchType(determineSearchType(searchRequest));

            searchHistoryRepository.save(history);
        } catch (Exception e) {
            log.error("Error saving search history", e);
        }
    }

    @Override
    public List<String> getAvailableJurisdictions() {
        return ipAssetRepository.findDistinctJurisdictions();
    }

    @Override
    public List<String> getAvailablePatentOffices() {
        return ipAssetRepository.findDistinctPatentOffices();
    }

    @Override
    public List<String> getAvailableDataSources() {
        return apiClients.stream()
                .map(PatentOfficeApiClient::getDataSource)
                .collect(Collectors.toList());
    }

    private SearchResultDto combineSearchResults(List<CompletableFuture<SearchResultDto>> futures, SearchRequestDto searchRequest) {
        SearchResultDto combinedResult = new SearchResultDto();
        List<IpAssetDto> allAssets = new ArrayList<>();
        long totalElements = 0;
        StringBuilder dataSources = new StringBuilder();

        for (CompletableFuture<SearchResultDto> future : futures) {
            try {
                SearchResultDto result = future.get();
                allAssets.addAll(result.getAssets());
                totalElements += result.getTotalElements();

                if (dataSources.length() > 0) dataSources.append(", ");
                dataSources.append(result.getDataSource());
            } catch (Exception e) {
                log.error("Error getting search result from future", e);
            }
        }

        // Remove duplicates based on externalId and patentOffice
        Map<String, IpAssetDto> uniqueAssets = new HashMap<>();
        for (IpAssetDto asset : allAssets) {
            String key = asset.getExternalId() + "_" + asset.getPatentOffice();
            uniqueAssets.put(key, asset);
        }

        combinedResult.setAssets(new ArrayList<>(uniqueAssets.values()));
        combinedResult.setTotalElements(totalElements);
        combinedResult.setCurrentPage(searchRequest.getPage());
        combinedResult.setPageSize(searchRequest.getSize());
        combinedResult.setTotalPages((int) Math.ceil((double) totalElements / searchRequest.getSize()));
        combinedResult.setHasNext(searchRequest.getPage() < combinedResult.getTotalPages() - 1);
        combinedResult.setHasPrevious(searchRequest.getPage() > 0);
        combinedResult.setSearchQuery(searchRequest.getQuery());
        combinedResult.setDataSource(dataSources.toString());

        return combinedResult;
    }

    private SearchResultDto createEmptyResult(SearchRequestDto searchRequest, String dataSource) {
        SearchResultDto result = new SearchResultDto();
        result.setDataSource(dataSource);
        result.setSearchQuery(searchRequest.getQuery());
        result.setCurrentPage(searchRequest.getPage());
        result.setPageSize(searchRequest.getSize());
        result.setAssets(new ArrayList<>());
        result.setTotalElements(0);
        result.setTotalPages(0);
        result.setHasNext(false);
        result.setHasPrevious(false);
        return result;
    }

    private SearchHistory.SearchType determineSearchType(SearchRequestDto searchRequest) {
        if (searchRequest.getInventor() != null && !searchRequest.getInventor().isEmpty()) {
            return SearchHistory.SearchType.INVENTOR;
        }
        if (searchRequest.getAssignee() != null && !searchRequest.getAssignee().isEmpty()) {
            return SearchHistory.SearchType.ASSIGNEE;
        }
        if (searchRequest.getClassification() != null && !searchRequest.getClassification().isEmpty()) {
            return SearchHistory.SearchType.CLASSIFICATION;
        }
        if (hasMultipleFilters(searchRequest)) {
            return SearchHistory.SearchType.ADVANCED;
        }
        return SearchHistory.SearchType.KEYWORD;
    }

    private boolean hasMultipleFilters(SearchRequestDto searchRequest) {
        int filterCount = 0;
        if (searchRequest.getQuery() != null && !searchRequest.getQuery().isEmpty()) filterCount++;
        if (searchRequest.getTitle() != null && !searchRequest.getTitle().isEmpty()) filterCount++;
        if (searchRequest.getInventor() != null && !searchRequest.getInventor().isEmpty()) filterCount++;
        if (searchRequest.getAssignee() != null && !searchRequest.getAssignee().isEmpty()) filterCount++;
        if (searchRequest.getJurisdiction() != null) filterCount++;
        if (searchRequest.getAssetType() != null) filterCount++;
        if (searchRequest.getFromDate() != null || searchRequest.getToDate() != null) filterCount++;
        return filterCount > 2;
    }

    private IpAssetDto convertToDto(IpAsset asset) {
        IpAssetDto dto = new IpAssetDto();
        BeanUtils.copyProperties(asset, dto);
        return dto;
    }

    private IpAsset convertToEntity(IpAssetDto dto) {
        IpAsset asset = new IpAsset();
        BeanUtils.copyProperties(dto, asset);
        return asset;
    }

    private void updateAssetFromDto(IpAsset asset, IpAssetDto dto) {
        asset.setTitle(dto.getTitle());
        asset.setDescription(dto.getDescription());
        asset.setStatus(dto.getStatus());
        asset.setInventor(dto.getInventor());
        asset.setAssignee(dto.getAssignee());
        asset.setKeywords(dto.getKeywords());
        asset.setLegalStatus(dto.getLegalStatus());
        // Update other fields as needed
    }
}