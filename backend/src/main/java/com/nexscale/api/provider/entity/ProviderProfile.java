package com.nexscale.api.provider.entity;

import com.nexscale.api.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity JPA yang merepresentasikan tabel 'provider_profiles'.
 * Menyimpan data spesialis, konsultan, atau agensi AI dengan role PROVIDER.
 * Terhubung secara 1:1 unidirectional terhadap entitas User via foreign key user_id.
 */
@Entity
@Table(name = "provider_profiles")
public class ProviderProfile {

    /**
     * Primary Key bertipe UUID yang di-generate otomatis oleh JPA/Hibernate.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "provider_id", updatable = false, nullable = false)
    private UUID providerId;

    /**
     * Relasi 1:1 ke entitas User.
     * - fetch = FetchType.LAZY: Mencegah query JOIN tidak perlu saat membaca ProviderProfile saja.
     * - unique = true: Menjamin relasi 1:1 di level database (1 user = tepat 1 profil).
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, unique = true)
    private User user;

    /**
     * Tipe provider: misalnya 'INDIVIDUAL', 'FREELANCER', atau 'AGENCY'.
     */
    @Column(name = "provider_type", length = 50)
    private String providerType;

    /**
     * Nama perusahaan atau studio konsultan (opsional bagi freelancer individu).
     */
    @Column(name = "company_name", length = 200)
    private String companyName;

    /**
     * Bio / deskripsi diri profesional atau keahlian utama provider AI.
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    /**
     * Lokasi kota atau domisili provider (misal: "Jakarta, Indonesia").
     */
    @Column(name = "location", length = 150)
    private String location;

    /**
     * Lama pengalaman kerja dalam tahun.
     * Menggunakan tipe wrapper 'Integer' agar dapat bernilai null jika belum diisi.
     */
    @Column(name = "experience_years")
    private Integer experienceYears;

    /**
     * Status ketersediaan menerima project (misal: 'AVAILABLE', 'BUSY', 'NOT_AVAILABLE').
     */
    @Column(name = "availability_status", length = 30)
    private String availabilityStatus;

    /**
     * Status verifikasi akun provider (misal: 'UNVERIFIED', 'PENDING', 'VERIFIED').
     */
    @Column(name = "verification_status", length = 30)
    private String verificationStatus;

    /**
     * Skor rating rata-rata provider (skala 1.00 s/d 5.00).
     * Menggunakan BigDecimal untuk menghindari rounding error dari floating-point binary.
     */
    @Column(name = "rating", precision = 3, scale = 2)
    private BigDecimal rating;

    /**
     * Timestamp waktu pembuatan record (diisi otomatis oleh Hibernate).
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp waktu terakhir update record (diperbarui otomatis oleh Hibernate).
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ==========================================
    // CONSTRUCTORS
    // ==========================================

    /**
     * Constructor tanpa argumen wajib disediakan untuk JPA / Hibernate via refleksi.
     */
    public ProviderProfile() {
    }

    /**
     * Constructor stub khusus untuk proses auto-provisioning saat registrasi awal user PROVIDER.
     *
     * @param user entitas User yang baru saja disimpan.
     */
    public ProviderProfile(User user) {
        this.user = user;
    }

    /**
     * Constructor parameter penuh untuk inisialisasi lengkap atau unit test.
     */
    public ProviderProfile(User user, String providerType, String companyName, String bio,
                           String location, Integer experienceYears, String availabilityStatus,
                           String verificationStatus, BigDecimal rating) {
        this.user = user;
        this.providerType = providerType;
        this.companyName = companyName;
        this.bio = bio;
        this.location = location;
        this.experienceYears = experienceYears;
        this.availabilityStatus = availabilityStatus;
        this.verificationStatus = verificationStatus;
        this.rating = rating;
    }

    // ==========================================
    // GETTERS DAN SETTERS
    // ==========================================

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getProviderType() {
        return providerType;
    }

    public void setProviderType(String providerType) {
        this.providerType = providerType;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
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

    // ==========================================
    // EQUALS & HASHCODE
    // ==========================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProviderProfile that = (ProviderProfile) o;
        return Objects.equals(providerId, that.providerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(providerId);
    }
}

