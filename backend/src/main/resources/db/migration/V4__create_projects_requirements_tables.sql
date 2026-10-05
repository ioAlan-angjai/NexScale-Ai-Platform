--Migrasi V4 : Membuat tabel projects, project_requirements, dan junction project_skills
--Agregat Proyek marketplace AI, spesifikasi kebutuhan sistem, dan pemetaan keahlian teknis
--1. Tabel projects
CREATE TABLE projects (
    project_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    client_id UUID NOT NULL REFERENCES client_profiles(client_id) ON DELETE RESTRICT,
    category_id UUID NOT NULL REFERENCES categories(category_id) ON DELETE RESTRICT,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    budget_min DECIMAL(15,2),
    budget_max DECIMAL(15,2),
    duration_estimate INTEGER,
    status VARCHAR(30) DEFAULT 'DRAFT' NOT NULL CHECK (status IN ('DRAFT','OPEN',
    'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    deadline DATE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT chk_projects_budget CHECK (budget_min >= 0 AND budget_max >= budget_min)
);

--2. Tabel project_requirements
CREATE TABLE project_requirements (
    requirement_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    project_id UUID NOT NULL UNIQUE REFERENCES projects(project_id) ON DELETE CASCADE,
    functional_requirements TEXT,
    technical_requirements TEXT,
    ai_summary TEXT,
    estimated_duration INTEGER,
    estimated_budget DECIMAL(15,2),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL
);