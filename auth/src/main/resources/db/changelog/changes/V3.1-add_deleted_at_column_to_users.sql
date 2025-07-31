-- Add deleted_at column to users table
ALTER TABLE users
ADD COLUMN deleted_at TIMESTAMP;