-- Update user role column type
ALTER TABLE users
    ALTER COLUMN role TYPE VARCHAR(20);

-- Update user status column type
ALTER TABLE users
    ALTER COLUMN status TYPE VARCHAR(20);

-- Drop custom ENUM types
DROP TYPE IF EXISTS user_role;
DROP TYPE IF EXISTS user_status;