INSERT INTO customer_table (customer_id, name, surname, password, email, blocked,blocked_until,time_left_to_unblock,unblock_requested)
VALUES
    (1, 'John', 'Doe', 'password1', 'john.doe@example.com', false,null,null,false),
    (2, 'Jane', 'Smith', 'password2', 'jane.smith@example.com', false,null,null,false);

-- Insert test data into wallet_table
INSERT INTO wallet_table (wallet_id, account_name, account_number, description, status, balance, customer_id)
VALUES
    (1, 'Wallet 1', '1234567890', 'Test wallet 1', 'NEW', 1000000.00, 1),
    (2, 'Wallet 2', '9876543210', 'Test wallet 2', 'NEW', 50000.00, 1),
    (3, 'Wallet 3', '5678901234', 'Test wallet 3', 'NEW', 200000.00, 2);