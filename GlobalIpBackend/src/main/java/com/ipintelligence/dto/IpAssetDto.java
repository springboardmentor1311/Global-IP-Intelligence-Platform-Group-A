package com.ipintelligence.dto;

import com.ipintelligence.model.IpAsset;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class IpAssetDto {
    private Long id;
    private String externalId;
    private String title;
    private String description;
    private IpAsset.AssetType assetType;
    private String applicationNumber;
    private String publicationNumber;
    private LocalDate priorityDate;
    private LocalDate applicationDate;
    private LocalDate publicationDate;
    private LocalDate grantDate;
    private String status;
    private String jurisdiction;
    private String patentOffice;
    private String inventor;
    private String assignee;
    private String ipcClassification;
    private String cpcClassification;
    private String keywords;
    private String legalStatus;
    private LocalDate expiryDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}