CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255),
    enabled BOOLEAN
);

CREATE TABLE IF NOT EXISTS authorities (
    username VARCHAR(255),
    authority VARCHAR(255),
    CONSTRAINT fk_authorities_users FOREIGN KEY (username) REFERENCES users (username)
);
create unique index ix_auth_username on authorities (username,authority);