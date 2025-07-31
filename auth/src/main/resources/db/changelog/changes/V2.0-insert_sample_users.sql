-- Insert 4 random users, one per role
INSERT INTO users (
    username,
    email,
    password_hash,
    first_name,
    last_name,
    role,
    status,
    created_at,
    updated_at
) VALUES
      (
          'jdoe',
          'jdoe@example.com',
          '$2a$10$JbM7FeEOGk3zlmvEZyUDBODZYfDbvJ3QZsWqjv/m4WdOKzQgxj.y6', -- bcrypt for "password123"
          'John',
          'Doe',
          'SUPERVISOR',
          'ACTIVE',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'asmith',
          'asmith@example.com',
          '$2a$10$1EeyyMbV1UozRgq3NGDdU.TqxK2xYBCVvGZ1vYukQvhfZ3EtNdbxG', -- bcrypt for "secret456"
          'Alice',
          'Smith',
          'EXPERT',
          'ACTIVE',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'tlee',
          'tlee@example.com',
          '$2a$10$D5ay9UeEHRVw4e.8WOSKhuZzJYowU0MXB9BnflQAOJeU3e8K92Ji6', -- bcrypt for "hello789"
          'Tom',
          'Lee',
          'MENTOR',
          'ACTIVE',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      ),
      (
          'bwhite',
          'bwhite@example.com',
          '$2a$10$eBhOpPvmgxxnQh08mF2s0e0KRNa3FxwLZPPmxKecPLVVRH1yRYA6K', -- bcrypt for "internpass"
          'Betty',
          'White',
          'INTERN',
          'ACTIVE',
          CURRENT_TIMESTAMP,
          CURRENT_TIMESTAMP
      );
