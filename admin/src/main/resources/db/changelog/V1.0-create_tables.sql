-- Drop tasks table first because it depends on programs
DROP TABLE IF EXISTS tasks CASCADE;

-- Drop programs table before courses
DROP TABLE IF EXISTS programs CASCADE;

-- Drop courses last
DROP TABLE IF EXISTS courses CASCADE;

-- Create courses table
CREATE TABLE courses
(
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) NOT NULL,
    supervisor_id  BIGINT       NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP,
    deleted_at     TIMESTAMP
);

-- Create programs table
CREATE TABLE programs
(
    id           BIGSERIAL PRIMARY KEY,
    course_id    BIGINT    NOT NULL,
    expert_id    BIGINT    NOT NULL,
    description  TEXT      NOT NULL,
    approved     BOOLEAN   DEFAULT FALSE NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    deleted_at   TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Create tasks table
CREATE TABLE tasks
(
    id           BIGSERIAL    PRIMARY KEY,
    program_id   BIGINT       NOT NULL,
    mentor_id    BIGINT       NOT NULL,
    intern_id    BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    github_link  VARCHAR(500) NOT NULL,
    status       VARCHAR(50)  NOT NULL,
    created_at   TIMESTAMP    DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP,
    deleted_at   TIMESTAMP,
    FOREIGN KEY (program_id) REFERENCES programs (id) ON DELETE CASCADE ON UPDATE CASCADE
);
