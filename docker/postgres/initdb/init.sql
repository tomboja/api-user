-- Connect to users_db
\c users_db;

-- Enable pgcrypto for database-level encryption helpers (optional but recommended)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
    -- Primary Key
    id SERIAL PRIMARY KEY,

    -- User Information
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL, -- Store encrypted/hashed passwords here

    -- Status and Roles
    active_status BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    user_role VARCHAR(20) DEFAULT 'USER', -- e.g., 'USER', 'ADMIN'

    -- Auditing and Tracking
    created_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login_date TIMESTAMP WITH TIME ZONE,
    disable_date TIMESTAMP WITH TIME ZONE,

    -- Application Specific (examples)
    phone_number VARCHAR(20),
    profile_picture_url TEXT,
    failed_login_attempts INT DEFAULT 0
);

-- Index for faster lookups on email
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
