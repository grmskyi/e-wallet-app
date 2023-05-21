CREATE TABLE IF NOT EXISTS transaction_table
(
    transaction_id        INT PRIMARY KEY,
    transaction_type      VARCHAR(255)   NOT NULL,
    transaction_timestamp TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    amount                DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    post_balance          DECIMAL(10, 2) NOT NULL DEFAULT 0.0,
    description           VARCHAR(255),
    wallet_id             INT            NOT NULL,
    FOREIGN KEY (wallet_id) REFERENCES wallet_table (wallet_id)
);