package com.nexscale.api.client.entity;

import com.nexscale.api.user.entity.User;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "client_profiles")
public class ClientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id", updatable = false, nullable = false)
    private UUID clientId;

    // Relasi 1:1 ke User dengan FK user_id
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "business_type", length = 100)
    private String businessType;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "location", length = 150)
    private String location;

    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // 1. Constructor tanpa argumen
    public ClientProfile() {
    }

    // 2. Constructor parameter untuk inisialisasi profil baru
    public ClientProfile(User user, String companyName, String businessType, String industry,
                         String description, String location, String website) {
        this.user = user;
        this.companyName = companyName;
        this.businessType = businessType;
        this.industry = industry;
        this.description = description;
        this.location = location;
        this.website = website;
    }

    // 3. Getters and Setters
    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // 4. Equals dan HashCode berbasis Primary Key
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientProfile that = (ClientProfile) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }
}