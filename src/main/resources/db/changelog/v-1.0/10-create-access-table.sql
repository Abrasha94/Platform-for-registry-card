CREATE TABLE access
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    permission VARCHAR(255)                            NOT NULL,
    CONSTRAINT pk_access PRIMARY KEY (id)
);

INSERT INTO access(permission)
VALUES ('admin permission');

INSERT INTO access(permission)
VALUES ('moderator permission');

INSERT INTO access(permission)
VALUES ('accountant permission');

INSERT INTO access(permission)
VALUES ('user permission')
