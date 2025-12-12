
package com.ipintelligence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ip_assets")
@Getter
@Setter
@NoArgsConstructor
public class IpAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String title;

    // Remove @Lob and use @Column with length instead
    @Column(columnDefinition = "TEXT", length = 10000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetType assetType;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column(name = "publication_number")
    private String publicationNumber;

    @Column(name = "priority_date")
    private LocalDate priorityDate;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    @Column(name = "publication_date")
    private LocalDate publicationDate;

    @Column(name = "grant_date")
    private LocalDate grantDate;

    @Column
    private String status;

    @Column
    private String jurisdiction;

    @Column(name = "patent_office")
    private String patentOffice;

    @Column
    private String inventor;

    @Column
    private String assignee;

    @Column(name = "ipc_classification")
    private String ipcClassification;

    @Column(name = "cpc_classification")
    private String cpcClassification;

    // Remove @Lob here too
    @Column(length = 5000)
    private String keywords;

    @Column(name = "legal_status")
    private String legalStatus;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    // Keep @Lob only for raw_data as it's not used in searches
    @Lob
    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AssetType {
        PATENT, TRADEMARK, DESIGN, UTILITY_MODEL
    }
}