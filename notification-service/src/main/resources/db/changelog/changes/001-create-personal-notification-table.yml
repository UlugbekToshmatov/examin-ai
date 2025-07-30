databaseChangeLog:
  - changeSet:
      id: 1753883018374-1
      author: olmosbek
      changes:
        - createTable:
            tableName: personal_notification
            columns:
              - column:
                  name: id
                  type: BIGSERIAL
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: type
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: recipient_id
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: sender_id
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: subject
                  type: VARCHAR(500)
                  constraints:
                    nullable: false
              - column:
                  name: message
                  type: TEXT
                  constraints:
                    nullable: false
              - column:
                  name: status
                  type: VARCHAR(100)
                  constraints:
                    nullable: false
              - column:
                  name: created_at
                  type: TIMESTAMP WITH TIME ZONE
                  defaultValueComputed: CURRENT_TIMESTAMP
                  constraints:
                    nullable: false

        - sql:
            sql: ALTER TABLE personal_notification ADD CONSTRAINT chk_message_length CHECK (length(message) <= 50000);
