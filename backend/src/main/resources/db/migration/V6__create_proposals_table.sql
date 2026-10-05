--Migrasi V6 : Membuat tabel proposal (sistem penawaran dan bidding marketplace)
--Menghubungkan provider_profiles dan projects dengan batasan 1 proposal per provider per project

CREATE TABLE proposals (
    proposal_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(project_id) ON DELETE CASCADE,
    provider_id UUID NOT NULL REFERENCES provider_profiles(provider_id) ON DELETE CASCADE,
    cover_letter TEXT NOT NULL,
    proposed_price DECIMAL(15,2) NOT NULL CHECK (proposed_price > 0),
    estimated_duration INTEGER NOT NULL CHECK (estimated_duration > 0),
    status VARCHAR(30) DEFAULT 'SUBMITTED' NOT NULL CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'ACCEPTED', 'REJECTED', 'WITHDRAW')),
    submitted_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_proposals_project_provider UNIQUE (project_id, provider_id)
);