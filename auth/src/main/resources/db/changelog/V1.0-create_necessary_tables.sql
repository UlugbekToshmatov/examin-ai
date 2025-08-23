-- Create users table
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users
(
    id            BIGSERIAL    PRIMARY KEY,
    external_id   VARCHAR(255) NOT NULL UNIQUE,
    username      VARCHAR(255) NOT NULL UNIQUE,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    status        VARCHAR(20)  NOT NULL,
    image_url     VARCHAR(255) DEFAULT 'https://cdn-icons-png.flaticon.com/512/149/149071.png',
    created_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted_at    TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('SUPERVISOR', 'EXPERT', 'MENTOR', 'INTERN', 'ADMIN')),
    CONSTRAINT chk_users_status CHECK (status IN ('ACTIVE', 'PENDING_VERIFICATION', 'BLOCKED', 'DELETED'))
);

-- Create tokens table
DROP TABLE IF EXISTS tokens CASCADE;

CREATE TABLE tokens
(
    id         BIGSERIAL     PRIMARY KEY,
    jwt_token  VARCHAR(1024) NOT NULL UNIQUE,
    type       VARCHAR(50)   NOT NULL CHECK (type IN ('ACCESS_TOKEN', 'REFRESH_TOKEN', 'ACCOUNT_VERIFICATION_TOKEN', 'RESET_PASSWORD_TOKEN')),
    issued_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP     NOT NULL,
    revoked    BOOLEAN       DEFAULT FALSE,
    expired    BOOLEAN       DEFAULT FALSE,
    user_id    BIGINT        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);