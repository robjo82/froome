CREATE DATABASE IF NOT EXISTS froome;

USE froome;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     email VARCHAR(255) NOT NULL,
                                     address VARCHAR(255) NOT NULL,
                                     is_admin BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(255) NOT NULL,
                                        description VARCHAR(255),
                                        price DECIMAL(10, 2) NOT NULL,
                                        stock INT NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      order_date DATETIME(6) NOT NULL,
                                      status VARCHAR(255) NOT NULL,
                                      CONSTRAINT fk_user_order FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           order_id BIGINT NOT NULL,
                                           product_id BIGINT NOT NULL,
                                           quantity INT NOT NULL,
                                           price DECIMAL(10, 2) NOT NULL,
                                           CONSTRAINT fk_order_order_item FOREIGN KEY (order_id) REFERENCES orders(id),
                                           CONSTRAINT fk_product_order_item FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE IF NOT EXISTS payments (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        order_id BIGINT NOT NULL,
                                        payment_date DATETIME(6) NOT NULL,
                                        amount DECIMAL(10, 2) NOT NULL,
                                        CONSTRAINT fk_order_payment FOREIGN KEY (order_id) REFERENCES orders(id)
);
