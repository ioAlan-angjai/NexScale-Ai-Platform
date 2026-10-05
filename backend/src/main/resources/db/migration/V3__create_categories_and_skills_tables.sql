--Migrasi V3 : Membuat tabel master categories dan skills
--Master data taksonomi domain dan kapabilitas teknis platform

--1. Tabel categories
CREATE TABLE categories (
    category_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE skills (
    skill_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);