INSERT INTO customer_table (customer_id, name, surname, password, email, blocked)
VALUES (1, 'John', 'Doe', 'password123', 'john.doe@example.com',false),
       (2, 'Jane', 'Smith', 'password456', 'jane.smith@example.com',false)
on conflict (customer_id) do update set customer_id=EXCLUDED.customer_id;

INSERT INTO wallet_table (wallet_id, account_name, account_number, description, status, balance, customer_id)
VALUES (1, 'Checking', '1234567890', 'My checking account', 'NEW', 10000.00, 1),
       (2, 'Savings', '9876543210', 'My savings account', 'NEW', 5000.00, 2)
on conflict (wallet_id) do update set wallet_id=EXCLUDED.wallet_id;