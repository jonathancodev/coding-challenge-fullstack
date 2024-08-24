CREATE DATABASE IF NOT EXISTS user_service;

USE user_service;

CREATE TABLE IF NOT EXISTS users (
    id bigint(20) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    status tinyint(1) NOT NULL
) engine=InnoDB;

INSERT INTO users (username, password, status) VALUES ('admin', '$2a$10$IcKfAJBXt3YKLxE.mBZluuMH5SaqYVHqhEqmY7m06F31k5nmOG8J2', 1);
INSERT INTO users (username, password, status) VALUES ('other_admin', '$2a$10$IcKfAJBXt3YKLxE.mBZluuMH5SaqYVHqhEqmY7m06F31k5nmOG8J2', 1);