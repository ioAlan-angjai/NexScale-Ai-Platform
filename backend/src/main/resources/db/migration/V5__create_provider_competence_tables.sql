--Migrasi V5 : Membuat tabel ekosistem kompetensi provider
--Terdiri dari : portfolio (1:N),providers_skils(junction N:M) dan experience(1:N)

--1. Tabel providers_skills (Junction Table N:M antara provider dan Skills)
CREATE TABLE providers_skills (
    provider_id UUID NOT NULL REFERENCES provider_profiles(provider_id) ON DELETE CASCADE,
    skill_id UUID NOT NULL REFERENCES skills(skill_id) ON DELETE RESTRICT,
    proficiency VARCHAR(30) NOT NULL CHECK (proficiency IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED', 'EXPERT')),
    years_experience INTEGER DEFAULT 0 NOT NULL CHECK (years_experience >= 0), PRIMARY KEY (provider_id, skill_id)
);

--2. Tabel portfolios (Etalase Portfolio & Bukti Karya Provider)
CREATE TABLE portfolios (
    portfolio_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES provider_profiles(provider_id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    project_url TEXT,
    description TEXT,
    image_url TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

--3. Tabel experiences (Riwayat karir profesional formal provider)
CREATE TABLE experiences (
    experience_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES provider_profiles(provider_id) ON DELETE CASCADE,
    company_name VARCHAR(200) NOT NULL,
    position VARCHAR(150) NOT NULL,
    description TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    is_current BOOLEAN DEFAULT FALSE NOT NULL,
    CONSTRAINT chk_experiences_date_range CHECK (end_date IS NULL OR end_date >= start_date)
);