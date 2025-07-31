-- Create ENUM types
CREATE TYPE thread_action_type AS ENUM ('START_THREAD', 'STOP_THREAD');
CREATE TYPE thread_type AS ENUM (
    'EMAIL_CONFIRMATION_THREAD',
    'SUCCESSFUL_REGISTRATION_THREAD',
    'TASK_NOTIFICATION_THREAD',
    'ERROR_NOTIFICATION_THREAD'
);

-- Create personal_notification table
CREATE TABLE personal_notification
(
    id                  BIGSERIAL PRIMARY KEY,
    notification_type   VARCHAR(50)  NOT NULL,
    recipient_id        BIGINT       NOT NULL,
    sender_id           BIGINT       NOT NULL,
    title               VARCHAR(255) NOT NULL,
    content             TEXT         NOT NULL,
    notification_status VARCHAR(50)  NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at             TIMESTAMP
);

-- Create notification_thread table
CREATE TABLE notification_thread
(
    id                 BIGSERIAL PRIMARY KEY,
    admin_id           BIGINT             NOT NULL,
    thread_action_type thread_action_type NOT NULL,
    thread_type        thread_type        NOT NULL,
    description        VARCHAR(255),
    created_at         TIMESTAMP          NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create error_notification table
CREATE TABLE error_notification
(
    id             BIGSERIAL PRIMARY KEY,
    service_name   VARCHAR(255) NOT NULL,
    exception_name VARCHAR(255) NOT NULL,
    method_name    VARCHAR(255),
    endpoint       VARCHAR(255),
    status_code    INTEGER      NOT NULL,
    message        VARCHAR(512),
    stack_trace    TEXT         NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
