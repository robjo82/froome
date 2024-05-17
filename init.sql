CREATE DATABASE IF NOT EXISTS froome;

USE froome;

-- Table Product
CREATE TABLE IF NOT EXISTS Product (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL
    );

-- Table User
CREATE TABLE IF NOT EXISTS User (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    address VARCHAR(255)
    );

-- Table Order
CREATE TABLE IF NOT EXISTS `Order` (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       user_id INT NOT NULL,
                                       order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                       status VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES User(id)
    );

-- Table OrderItem
CREATE TABLE IF NOT EXISTS OrderItem (
                                         id INT AUTO_INCREMENT PRIMARY KEY,
                                         order_id INT NOT NULL,
                                         product_id INT NOT NULL,
                                         quantity INT NOT NULL,
                                         price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `Order`(id),
    FOREIGN KEY (product_id) REFERENCES Product(id)
    );

-- Table Payment
CREATE TABLE IF NOT EXISTS Payment (
                                       id INT AUTO_INCREMENT PRIMARY KEY,
                                       order_id INT NOT NULL,
                                       amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    payment_method VARCHAR(255) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES `Order`(id)
    );
