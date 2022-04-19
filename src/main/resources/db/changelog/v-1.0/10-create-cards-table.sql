CREATE TABLE cards
(
    number          BIGINT                      NOT NULL,
    status          VARCHAR(20)                 NOT NULL,
    type            VARCHAR(10)                 NOT NULL,
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    cvvcode         INTEGER                     NOT NULL,
    user_id         BIGINT,
    company_id      BIGINT,
    CONSTRAINT pk_cards PRIMARY KEY (number)
);

ALTER TABLE cards
    ADD CONSTRAINT FK_CARDS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES companies (id);

ALTER TABLE cards
    ADD CONSTRAINT FK_CARDS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);