CREATE TABLE IF NOT EXISTS customer_table
(
    customer_id BIGINT PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    surname     VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL,
    blocked     boolean NOT NULL DEFAULT false
);

CREATE TABLE IF NOT EXISTS wallet_table
(
    wallet_id      BIGINT PRIMARY KEY,
    account_name   VARCHAR(255)   NOT NULL,
    account_number VARCHAR(255)   NOT NULL,
    description    VARCHAR(255),
    status         VARCHAR(20)    NOT NULL DEFAULT 'NEW',
    balance        DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    customer_id    INT,
    FOREIGN KEY (customer_id) REFERENCES customer_table (customer_id)
);
