
package com.ipintelligence.controller;

import com.ipintelligence.dto.IpAssetDto;
import com.ipintelligence.dto.SearchRequestDto;
import com.ipintelligence.dto.SearchResultDto;
import com.ipintelligence.model.IpAsset;
import com.ipintelligence.model.SearchHistory;
import com.ipintelligence.model.User;
import com.ipintelligence.service.IpSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    private final IpSearchService ipSearchService;

    public SearchController(IpSearchService ipSearchService) {
        this.ipSearchService = ipSearchService;
    }

    @PostMapping("/all")
    public ResponseEntity<SearchResultDto> searchAllSources(@RequestBody SearchRequestDto searchRequest,
                                                            @AuthenticationPrincipal User currentUser) {
        log.info("Searching all sources for query: {}", searchRequest.getQuery());
        SearchResultDto result = ipSearchService.searchAcrossAllSources(searchRequest, currentUser);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/source/{dataSource}")
    public ResponseEntity<SearchResultDto> searchSpecificSource(@PathVariable String dataSource,
                                                                @RequestBody SearchRequestDto searchRequest,
                                                                @AuthenticationPrincipal User currentUser) {
        log.info("Searching {} for query: {}", dataSource, searchRequest.getQuery());
        SearchResultDto result = ipSearchService.searchSpecificSource(searchRequest, dataSource, currentUser);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/local")
    public ResponseEntity<Page<IpAsset>> searchLocalDatabase(@RequestBody SearchRequestDto searchRequest) {
        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.Direction.fromString(searchRequest.getSortDirection()),
                searchRequest.getSortBy()
        );

        Page<IpAsset> result = ipSearchService.searchLocalDatabase(searchRequest, pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<IpAssetDto> getAssetDetails(@PathVariable Long assetId) {
        IpAssetDto asset = ipSearchService.getAssetDetails(assetId);
        if (asset != null) {
            return ResponseEntity.ok(asset);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/asset/{dataSource}/{externalId}")
    public ResponseEntity<IpAssetDto> getAssetByExternalId(@PathVariable String dataSource,
                                                           @PathVariable String externalId) {
        IpAssetDto asset = ipSearchService.getAssetDetailsByExternalId(externalId, dataSource);
        if (asset != null) {
            return ResponseEntity.ok(asset);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<SearchHistory>> getUserSearchHistory(@AuthenticationPrincipal User currentUser,
                                                                    @RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SearchHistory> history = ipSearchService.getUserSearchHistory(currentUser, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/filters/jurisdictions")
    public ResponseEntity<List<String>> getAvailableJurisdictions() {
        List<String> jurisdictions = ipSearchService.getAvailableJurisdictions();
        return ResponseEntity.ok(jurisdictions);
    }

    @GetMapping("/filters/patent-offices")
    public ResponseEntity<List<String>> getAvailablePatentOffices() {
        List<String> patentOffices = ipSearchService.getAvailablePatentOffices();
        return ResponseEntity.ok(patentOffices);
    }

    @GetMapping("/filters/data-sources")
    public ResponseEntity<List<String>> getAvailableDataSources() {
        List<String> dataSources = ipSearchService.getAvailableDataSources();
        return ResponseEntity.ok(dataSources);
    }

    @GetMapping("/filters/asset-types")
    public ResponseEntity<IpAsset.AssetType[]> getAvailableAssetTypes() {
        return ResponseEntity.ok(IpAsset.AssetType.values());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getSearchStats(@AuthenticationPrincipal User currentUser) {
        // This could include user's search statistics, trending searches, etc.
        return ResponseEntity.ok(Map.of(
                "message", "Search statistics endpoint - to be implemented",
                "totalSearches", 0,
                "recentSearches", 0
        ));
    }
}