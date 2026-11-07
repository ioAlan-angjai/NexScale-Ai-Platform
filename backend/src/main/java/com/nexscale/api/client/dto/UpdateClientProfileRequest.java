package com.nexscale.api.client.dto;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) untuk menerima permintaan pembaruan data profil klien.
 * Menggunakan Jakarta Bean Validation untuk menjaga batas panjang karakter sesuai skema SQL.
 */
public class UpdateClientProfileRequest {

    @Size(max = 200, message = "Nama perusahaan maksimal 200 karakter")
    private String companyName;

    @Size(max = 100, message = "Tipe bisnis maksimal 100 karakter")
    private String businessType;

    @Size(max = 100, message = "Industri maksimal 100 karakter")
    private String industry;

    private String description;

    @Size(max = 150, message = "Lokasi maksimal 150 karakter")
    private String location;

    private String website;

    public UpdateClientProfileRequest() {
    }

    public UpdateClientProfileRequest(String companyName, String businessType, String industry,
                                      String description, String location, String website) {
        this.companyName = companyName;
        this.businessType = businessType;
        this.industry = industry;
        this.description = description;
        this.location = location;
        this.website = website;
    }

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
}

