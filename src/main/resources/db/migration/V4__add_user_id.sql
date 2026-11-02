ALTER TABLE users
    ADD COLUMN user_id VARCHAR(32);

UPDATE users
SET user_id = 'LEGACY' || id
WHERE user_id IS NULL;

ALTER TABLE users
    ALTER COLUMN user_id SET NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT uk_users_user_id UNIQUE (user_id);
