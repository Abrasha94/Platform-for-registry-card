CREATE TABLE users
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(55)                             NOT NULL,
    password     VARCHAR                                 NOT NULL,
    status       VARCHAR(10) DEFAULT 'ACTIVE'            NOT NULL,
    companies_id BIGINT                                  NOT NULL,
    role_id      BIGINT                                  NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);


ALTER TABLE users
    ADD CONSTRAINT uc_users_name UNIQUE (name);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_COMPANIES FOREIGN KEY (companies_id) REFERENCES companies (id);

ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (id);

INSERT INTO users(name, password, companies_id, role_id)
VALUES ('admin', '$2a$12$AMdnoL4ygOfgavUCp9h3tO9/BKH7NZgMZxjwhTPg5yrL1lCGSXTCy', 1, 1);