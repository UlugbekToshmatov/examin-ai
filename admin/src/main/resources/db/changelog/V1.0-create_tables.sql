-- Drop task_interns table first because it depends on tasks
DROP TABLE IF EXISTS task_interns CASCADE;

-- Drop tasks table first because it depends on course
DROP TABLE IF EXISTS tasks CASCADE;

-- Drop course table before program
DROP TABLE IF EXISTS courses CASCADE;

-- Drop program table last
DROP TABLE IF EXISTS programs CASCADE;

-- Create program table
CREATE TABLE programs
(
    id             BIGSERIAL    PRIMARY KEY,
    name           VARCHAR(255) NOT NULL UNIQUE,
    supervisor_id  UUID         NOT NULL,
    status         VARCHAR(50)  NOT NULL DEFAULT 'CREATED',
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     UUID,
    updated_at     TIMESTAMP,
    updated_by     UUID,
    deleted_at     TIMESTAMP,
    deleted_by     UUID
);

-- Create course table
CREATE TABLE courses
(
    id           BIGSERIAL    PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(500) NOT NULL,
    program_id   BIGINT       NOT NULL,
    expert_id    BIGINT       NOT NULL,
    status       VARCHAR(50)  NOT NULL DEFAULT 'CREATED',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   UUID,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,
    FOREIGN KEY (program_id) REFERENCES programs (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_courses_program_id_name UNIQUE (program_id, name)
);

-- Create tasks table
CREATE TABLE tasks
(
    id           BIGSERIAL    PRIMARY KEY,
    course_id    BIGINT       NOT NULL,
    mentor_id    UUID         NOT NULL,
    title        VARCHAR(255) NOT NULL,
    definition   VARCHAR(500) NOT NULL,
    status       VARCHAR(50)  NOT NULL DEFAULT 'IN_PROGRESS',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   UUID,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,
    FOREIGN KEY (course_id) REFERENCES courses (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_tasks_course_id_title UNIQUE (course_id, title)
);

-- Create task_interns table
CREATE TABLE task_interns
(
    id           BIGSERIAL    PRIMARY KEY,
    task_id      BIGINT       NOT NULL,
    intern_id    UUID         NOT NULL,
    github_link  VARCHAR(500) NOT NULL DEFAULT 'NOT SUBMITTED',
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   UUID,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,
    FOREIGN KEY (task_id) REFERENCES tasks (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT unq_task_interns_task_id_intern_id UNIQUE (task_id, intern_id)
);

