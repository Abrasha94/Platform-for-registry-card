CREATE TABLE cards
(
    number          BIGINT                      NOT NULL,
    status          VARCHAR(20)                 NOT NULL,
    type            VARCHAR(10)                 NOT NULL,
    pay_system      VARCHAR(10)                 NOT NULL,
    company_id      BIGINT,
    CONSTRAINT pk_cards PRIMARY KEY (number)
);

ALTER TABLE cards
    ADD CONSTRAINT FK_CARDS_ON_COMPANY FOREIGN KEY (company_id) REFERENCES companies (id);