-- Create users table
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id            BIGSERIAL    PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    image_url     VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP,
    CONSTRAINT unq_users_email UNIQUE (email),
    CONSTRAINT chk_users_role CHECK (role IN ('SUPERVISOR', 'EXPERT', 'MENTOR', 'INTERN')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'PENDING_VERIFICATION', 'BLOCKED', 'DELETED'))
);

-- Create account verifications table
DROP TABLE IF EXISTS account_verifications CASCADE;

CREATE TABLE account_verifications
(
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    url        VARCHAR(255) NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_account_verifications_user_id_url UNIQUE (user_id, url),
    CONSTRAINT chk_account_verifications_status CHECK (status IN ('ACTIVE', 'DELETED'))
);

-- Create tokens table
DROP TABLE IF EXISTS tokens CASCADE;

CREATE TABLE tokens
(
    id         BIGSERIAL     PRIMARY KEY,
    jwt_token  VARCHAR(1024) NOT NULL UNIQUE,
    type       VARCHAR(15)   NOT NULL CHECK (type IN ('ACCESS_TOKEN', 'REFRESH_TOKEN')),
    issued_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP     NOT NULL,
    revoked    BOOLEAN       DEFAULT FALSE,
    expired    BOOLEAN       DEFAULT FALSE,
    user_id    BIGINT        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);