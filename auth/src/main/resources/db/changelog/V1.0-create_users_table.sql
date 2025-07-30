-- Create ENUM types
CREATE TYPE user_role AS ENUM ('SUPERVISOR', 'EXPERT', 'MENTOR', 'INTERN');
CREATE TYPE user_status AS ENUM ('ACTIVE', 'PENDING', 'BLOCKED', 'DELETED');

-- Create users table
CREATE TABLE users
(
    id            BIGSERIAL PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    username      VARCHAR(255) NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          user_role    NOT NULL,
    status        user_status  NOT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Optional: Indexes (already implied by UNIQUE, but added explicitly for clarity/performance)
CREATE INDEX idx_users_username ON users (username);
CREATE INDEX idx_users_email ON users (email);