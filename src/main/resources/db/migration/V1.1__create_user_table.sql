CREATE TABLE users
(
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255),
    username VARCHAR(255),
    password VARCHAR(255)
);