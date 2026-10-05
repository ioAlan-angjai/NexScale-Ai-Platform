--Migrasi V2 : Membuat tabel profil CLIENT dan PROVIDER
--Setiap profil terhubung 1:1 dengan users melalui user_id

--1. Tabel client_profiles
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

--2. Tabel provider_profiles
CREATE TABLE provider_profiles(
    provider_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE REFERENCES users(user_id) ON DELETE CASCADE,
    provider_type VARCHAR(50),
    company_name VARCHAR(200),
    bio TEXT,
    location VARCHAR(150),
    experience_years INTEGER,
    availability_status VARCHAR(30),
    verification_status VARCHAR(30),
    rating DECIMAL(3,2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);