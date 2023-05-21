ALTER TABLE customer_table
    ADD COLUMN blocked_until TIMESTAMP;
ALTER TABLE customer_table
    ADD COLUMN time_left_to_unblock TIMESTAMP;
ALTER TABLE customer_table
    ADD COLUMN unblock_requested boolean NOT NULL DEFAULT false;