-- 1. Abilita l'estensione per criptare le password
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 2. Crea la struttura della tabella
CREATE TABLE IF NOT EXISTS app_users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL
);

-- 3. Inserisce l'ADMIN solo se non esiste
INSERT INTO app_users (email, password, role)
VALUES (
    'admin@hr.com',
    crypt('admin123', gen_salt('bf')), -- Genera hash compatibile con Spring Security
    'HR_ADMIN'
)
ON CONFLICT (email) DO NOTHING;