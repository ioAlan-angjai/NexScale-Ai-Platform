package com.nexscale.api.provider.dto;

import com.nexscale.api.provider.entity.ProviderProfile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object (DTO) untuk menampilkan data profil penyedia jasa AI.
 */
public class ProviderProfileResponse {

    private UUID providerId;
    private UUID userId;
    private String providerType;
    private String companyName;
    private String bio;
    private String location;
    private Integer experienceYears;
    private String availabilityStatus;
    private String verificationStatus;
    private BigDecimal rating;
    private Instant createdAt;
    private Instant updatedAt;

    public ProviderProfileResponse() {
    }

    public ProviderProfileResponse(UUID providerId, UUID userId, String providerType, String companyName,
                                   String bio, String location, Integer experienceYears,
                                   String availabilityStatus, String verificationStatus, BigDecimal rating,
                                   Instant createdAt, Instant updatedAt) {
        this.providerId = providerId;
        this.userId = userId;
        this.providerType = providerType;
        this.companyName = companyName;
        this.bio = bio;
        this.location = location;
        this.experienceYears = experienceYears;
        this.availabilityStatus = availabilityStatus;
        this.verificationStatus = verificationStatus;
        this.rating = rating;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method konversi dari entitas ProviderProfile ke DTO ProviderProfileResponse.
     */
    public static ProviderProfileResponse fromEntity(ProviderProfile profile) {
        return new ProviderProfileResponse(
                profile.getProviderId(),
                profile.getUser().getUserId(),
                profile.getProviderType(),
                profile.getCompanyName(),
                profile.getBio(),
                profile.getLocation(),
                profile.getExperienceYears(),
                profile.getAvailabilityStatus(),
                profile.getVerificationStatus(),
                profile.getRating(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    public UUID getProviderId() { return providerId; }
    public void setProviderId(UUID providerId) { this.providerId = providerId; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getExperienceYears() { return experienceYears; }
    public void setExperienceYears(Integer experienceYears) { this.experienceYears = experienceYears; }

    public String getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(String availabilityStatus) { this.availabilityStatus = availabilityStatus; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}

