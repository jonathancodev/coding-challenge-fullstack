CREATE DATABASE IF NOT EXISTS operation_service;

USE operation_service;

CREATE TABLE IF NOT EXISTS operations (
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    operation_type tinyint(1) NOT NULL,
    cost decimal(19,2) NOT NULL
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS records (
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    transaction_id VARCHAR(255) NOT NULL,
    operation_id bigint(20) NOT NULL,
    user_id bigint(20) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    user_balance DECIMAL(19, 2) NOT NULL,
    operation_response TEXT NOT NULL,
    operation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_record_operation FOREIGN KEY (operation_id) REFERENCES operations(id)
) engine=InnoDB;

INSERT INTO operations (operation_type, cost) VALUES (0, 0.1), (1, 0.1), (2, 0.2), (3, 0.2), (4, 0.3), (5, 0.5);