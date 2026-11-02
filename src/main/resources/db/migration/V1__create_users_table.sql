CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       role VARCHAR(50),
                       status VARCHAR(50)
);

ALTER TABLE users
    ADD COLUMN password VARCHAR(255);

ALTER TABLE users
    ADD COLUMN phone VARCHAR(20);

ALTER TABLE users
    ADD COLUMN location VARCHAR(255);

ALTER TABLE users
    ADD COLUMN department VARCHAR(255);