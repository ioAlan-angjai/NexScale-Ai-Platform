package com.nexscale.api.client.dto;

import com.nexscale.api.client.entity.ClientProfile;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) untuk mengirimkan data profil klien ke client/frontend.
 * Memisahkan layer representasi dari layer entitas database.
 */
public class ClientProfileResponse {

    private UUID clientId;
    private UUID userId;
    private String companyName;
    private String businessType;
    private String industry;
    private String description;
    private String location;
    private String website;
    private Instant createdAt;
    private Instant updatedAt;

    public ClientProfileResponse() {
    }

    public ClientProfileResponse(UUID clientId, UUID userId, String companyName, String businessType,
                                 String industry, String description, String location, String website,
                                 Instant createdAt, Instant updatedAt) {
        this.clientId = clientId;
        this.userId = userId;
        this.companyName = companyName;
        this.businessType = businessType;
        this.industry = industry;
        this.description = description;
        this.location = location;
        this.website = website;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Static Factory Method untuk memetakan entitas ClientProfile ke DTO ClientProfileResponse.
     * Mencegah kebocoran struktur entitas internal ke API publik.
     */
    public static ClientProfileResponse fromEntity(ClientProfile profile) {
        return new ClientProfileResponse(
                profile.getClientId(),
                profile.getUser().getUserId(),
                profile.getCompanyName(),
                profile.getBusinessType(),
                profile.getIndustry(),
                profile.getDescription(),
                profile.getLocation(),
                profile.getWebsite(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public UUID getClientId() { return clientId; }
    public void setClientId(UUID clientId) { this.clientId = clientId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

