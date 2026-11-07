package com.nexscale.api.provider.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) untuk validasi request pembaruan profil provider.
 */
public class UpdateProviderProfileRequest {

    @Size(max = 50, message = "Tipe provider maksimal 50 karakter")
    private String providerType;

    @Size(max = 200, message = "Nama perusahaan maksimal 200 karakter")
    private String companyName;

    private String bio;

    @Size(max = 150, message = "Lokasi maksimal 150 karakter")
    private String location;

    @Min(value = 0, message = "Tahun pengalaman tidak boleh bernilai negatif")
    private Integer experienceYears;

    @Size(max = 30, message = "Status ketersediaan maksimal 30 karakter")
    private String availabilityStatus;

    public UpdateProviderProfileRequest() {
    }

    public UpdateProviderProfileRequest(String providerType, String companyName, String bio,
                                        String location, Integer experienceYears, String availabilityStatus) {
        this.providerType = providerType;
        this.companyName = companyName;
        this.bio = bio;
        this.location = location;
        this.experienceYears = experienceYears;
        this.availabilityStatus = availabilityStatus;
    }

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
}

