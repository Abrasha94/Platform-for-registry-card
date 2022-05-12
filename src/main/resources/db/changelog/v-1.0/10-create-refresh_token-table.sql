CREATE TABLE refresh_token
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    token       VARCHAR(36)                             NOT NULL,
    expire_date TIMESTAMP                               NOT NULL,
    user_id     BIGINT                                  NOT NULL,
    CONSTRAINT pk_refresh_token PRIMARY KEY (id)
);

ALTER TABLE refresh_token
    ADD CONSTRAINT uc_refresh_token_name UNIQUE (token);

ALTER TABLE refresh_token
    ADD CONSTRAINT FK_REFRESH_TOKEN_ON_USERS FOREIGN KEY (user_id) REFERENCES users (id);