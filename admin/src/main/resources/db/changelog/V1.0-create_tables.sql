-- Drop tasks_interns table first because it depends on tasks
DROP TABLE IF EXISTS tasks_interns CASCADE;

-- Drop tasks table first because it depends on courses
DROP TABLE IF EXISTS tasks CASCADE;

-- Drop courses_mentors table before courses
DROP TABLE IF EXISTS courses_mentors CASCADE;

-- Drop courses table before programs because it depends on programs
DROP TABLE IF EXISTS courses CASCADE;

-- Drop programs_experts table before program
DROP TABLE IF EXISTS programs_experts CASCADE;

-- Drop programs table last
DROP TABLE IF EXISTS programs CASCADE;

-- Create programs table
CREATE TABLE programs
(
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    description    VARCHAR(255) NOT NULL,
    status         VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     UUID,
    updated_at     TIMESTAMP,
    updated_by     UUID,
    deleted_at     TIMESTAMP,
    deleted_by     UUID
);

-- Create programs_experts table
CREATE TABLE programs_experts
(
    id               BIGSERIAL    PRIMARY KEY,
    program_id       BIGINT       NOT NULL,
    expert_id        UUID         NOT NULL,
    expert_full_name VARCHAR(255) NOT NULL,
    expert_email     VARCHAR(255) NOT NULL,
    status           VARCHAR(50)  NOT NULL DEFAULT 'ASSIGNED',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID,
    updated_at       TIMESTAMP,
    updated_by       UUID,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,
    FOREIGN KEY (program_id) REFERENCES programs (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_programs_experts_program_id_expert_id UNIQUE (program_id, expert_id)
);

-- Create course table
CREATE TABLE courses
(
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(500) NOT NULL,
    program_id   BIGINT       NOT NULL,
    status       VARCHAR(50)  NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   UUID,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,
    FOREIGN KEY (program_id) REFERENCES programs (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_courses_program_id_name UNIQUE (program_id, name)
);

-- Create courses_mentors table
CREATE TABLE courses_mentors
(
    id               BIGSERIAL    PRIMARY KEY,
    course_id       BIGINT       NOT NULL,
    mentor_id        UUID         NOT NULL,
    mentor_full_name VARCHAR(255) NOT NULL,
    mentor_email     VARCHAR(255) NOT NULL,
    status           VARCHAR(50)  NOT NULL DEFAULT 'ASSIGNED',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID,
    updated_at       TIMESTAMP,
    updated_by       UUID,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,
    FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_courses_mentors_course_id_mentor_id UNIQUE (course_id, mentor_id)
);

-- Create tasks table
CREATE TABLE tasks
(
    id           BIGSERIAL    PRIMARY KEY,
    course_id    BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    definition   VARCHAR(500) NOT NULL,
    status       VARCHAR(50)  NOT NULL DEFAULT 'PENDING_APPROVAL',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   UUID,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,
    FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_tasks_course_id_title UNIQUE (course_id, title)
);

-- Create tasks_interns table
CREATE TABLE tasks_interns
(
    id               BIGSERIAL    PRIMARY KEY,
    task_id          BIGINT       NOT NULL,
    intern_id        UUID         NOT NULL,
    intern_full_name VARCHAR(255) NOT NULL,
    intern_email     VARCHAR(255) NOT NULL,
    github_link      VARCHAR(500) NOT NULL DEFAULT 'NOT SUBMITTED',
    task_status      VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    status           VARCHAR(50) NOT NULL DEFAULT 'ASSIGNED',
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by       UUID,
    updated_at       TIMESTAMP,
    updated_by       UUID,
    deleted_at       TIMESTAMP,
    deleted_by       UUID,
    FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_tasks_interns_task_id_intern_id UNIQUE (task_id, intern_id)
);

