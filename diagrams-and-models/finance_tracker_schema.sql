DROP SCHEMA IF EXISTS finance_tracker;
CREATE SCHEMA finance_tracker COLLATE = utf8_general_ci;

USE finance_tracker;

/* *************************************************************** 
***************************CREATING TABLES************************
**************************************************************** */

CREATE TABLE users (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(80) NOT NULL,
    last_name VARCHAR(80) NOT NULL,
    username VARCHAR(80) NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE accounts (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    balance DECIMAL NOT NULL,
    acc_limit DECIMAL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    owner_id INT NOT NULL,
    CONSTRAINT user_acc_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE categories (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    type VARCHAR(80) NOT NULL,
    owner_id INT,
    CONSTRAINT users_category_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE budgets (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    label VARCHAR(80) NOT NULL,
    amount DECIMAL NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(200),
    account_id INT NOT NULL,
    owner_id INT NOT NULL,
    CONSTRAINT acc_budget_fk FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_budget_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE transactions (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(80) NOT NULL,
    amount DECIMAL NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(200),
    account_id INT NOT NULL,
    category_id INT NOT NULL,
    owner_id INT NOT NULL,
    CONSTRAINT acc_transaction_fk FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT category_transaction_fk FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_transaction_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE planned_payments (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL,
    payment_type VARCHAR(80) NOT NULL,
    frequency INT NOT NULL,
    duration_unit VARCHAR(20) NOT NULL,
    amount DECIMAL NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    due_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    description VARCHAR(200),
    account_id INT NOT NULL,
    category_id INT NOT NULL,
    owner_id INT NOT NULL,
    CONSTRAINT acc_planned_pay_fk FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT category_planned_pay_fk FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT user_planned_pay_fk FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE budgets_have_transactions (
	budget_id INT NOT NULL,
    transaction_id INT NOT NULL,
    PRIMARY KEY (budget_id, transaction_id),
	CONSTRAINT bht_budget_fk FOREIGN KEY (budget_id) REFERENCES budgets(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT bht_transaction_fk FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE category_images (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
	url VARCHAR(200) NOT NULL,
	category_id INT NOT NULL UNIQUE KEY,
	INDEX ci_category_fk_idx (category_id ASC),
	CONSTRAINT ci_category_fk FOREIGN KEY (category_id) REFERENCES categories(id)
    ON DELETE CASCADE
    ON UPDATE CASCADE
);

CREATE TABLE data_logs (
	id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(80) NOT NULL,
    overall_balance DOUBLE NOT NULL,
    accounts_count INT NOT NULL,
    transactions_count INT NOT NULL,
    planned_payments_count INT NOT NULL,
    budgets_count INT NOT NULL
);