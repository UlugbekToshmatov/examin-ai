-- Make updated_at column nullable
ALTER TABLE users
ALTER COLUMN updated_at DROP NOT NULL;