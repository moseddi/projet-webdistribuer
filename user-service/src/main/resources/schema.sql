CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(255) UNIQUE,
    telephone VARCHAR(20),
    adresse TEXT,
    ville VARCHAR(100),
    pays VARCHAR(100),
    password VARCHAR(255),
    role VARCHAR(20),
    actif BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    reset_token VARCHAR(255),
    reset_token_expiration DATETIME
);
