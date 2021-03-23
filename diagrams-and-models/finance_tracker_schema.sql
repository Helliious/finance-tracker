DROP SCHEMA IF EXISTS finance_tracker;
CREATE SCHEMA finance_tracker COLLATE = utf8_general_ci;

USE finance_tracker;

/* *************************************************************** 
***************************CREATING TABLES************************
**************************************************************** */

CREATE TABLE users (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(80),
    last_name VARCHAR(80),
    username VARCHAR(80),
    password VARCHAR(100),
    email VARCHAR(100),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80),
    balance DECIMAL,
    acc_limit DECIMAL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    owner_id INT UNSIGNED NOT NULL,
    CONSTRAINT user_acc_fk FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE categories (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80),
    type VARCHAR(80)
);

CREATE TABLE budgets (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80),
    label VARCHAR(80),
    amount DECIMAL,
    due_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_id INT UNSIGNED NOT NULL,
    category_id INT UNSIGNED NOT NULL,
    owner_id INT UNSIGNED NOT NULL,
    CONSTRAINT acc_budget_fk FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT category_budget_fk FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT user_budget_fk FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE transactions (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(80),
    amount DECIMAL,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_id INT UNSIGNED NOT NULL,
    category_id INT UNSIGNED NOT NULL,
    owner_id INT UNSIGNED NOT NULL,
    CONSTRAINT acc_transaction_fk FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT category_transaction_fk FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT user_transaction_fk FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE planned_payments (
	id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80),
    payment_type VARCHAR(80),
    frequency INT,
    duration_unit VARCHAR(20),
    amount DECIMAL,
    due_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    account_id INT UNSIGNED NOT NULL,
    category_id INT UNSIGNED NOT NULL,
    owner_id INT UNSIGNED NOT NULL,
    CONSTRAINT acc_planned_pay_fk FOREIGN KEY (account_id) REFERENCES accounts(id),
    CONSTRAINT category_planned_pay_fk FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT user_planned_pay_fk FOREIGN KEY (owner_id) REFERENCES users(id)
);

CREATE TABLE budgets_have_transactions (
	budget_id INT UNSIGNED NOT NULL,
    transaction_id INT UNSIGNED NOT NULL,
    CONSTRAINT bht_budget_fk FOREIGN KEY (budget_id) REFERENCES budgets(id),
    CONSTRAINT bht_transaction_fk FOREIGN KEY (transaction_id) REFERENCES transactions(id)
);