--Migrasi v2 : Pembuatan Tabel Profil Client dan Provider

--1. Tabel Client
CREATE TABLE client_profiles (
    client_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    company_name VARCHAR(200),
    business_type VARCHAR(100),
    industry VARCHAR(100),
    description TEXT,
    location VARCHAR(150),
    website TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--2. Tabel Provider
CREATE TABLE provider_profiles (
    provider_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    provider_type VARCHAR(50),
    company_name VARCHAR(200),
    bio TEXT,
    location VARCHAR(150),
    experience_years INTEGER DEFAULT 0 NOT NULL,
    availability_status VARCHAR(30) DEFAULT 'AVAILABLE' NOT NULL CHECK(availability_status
    IN('AVAILABLE', 'BUSY', 'UNAVAILABLE')),
    verification_status VARCHAR(30) DEFAULT 'UNVERIFIED' NOT NULL CHECK(verification_status 
    IN('UNVERIFIED', 'PENDING', 'VERIFIED', 'REJECTED')),
    rating DECIMAL(3,2) DEFAULT 0.00 NOT NULL CHECK (rating >= 0.00 AND
    rating <= 5.00),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);