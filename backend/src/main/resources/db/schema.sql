-- Users Table
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username',
    password VARCHAR(100) NOT NULL COMMENT 'Encrypted Password',
    nickname VARCHAR(50) COMMENT 'Nickname',
    phone VARCHAR(20) UNIQUE COMMENT 'Phone Number',
    avatar VARCHAR(255) COMMENT 'Avatar URL',
    role VARCHAR(20) NOT NULL DEFAULT 'CLIENT' COMMENT 'Role: CLIENT, ADMIN, MERCHANT',
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-Active, 0-Disabled',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'User Table';

-- User Address Table
CREATE TABLE IF NOT EXISTS user_address (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT 'User ID',
    receiver_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    detail_address VARCHAR(255) NOT NULL,
    is_default TINYINT DEFAULT 0 COMMENT 'Is Default: 1-Yes, 0-No',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT 'Shipping Address';

-- Identity Authentication Table
CREATE TABLE IF NOT EXISTS user_auth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    real_name VARCHAR(50) NOT NULL,
    id_card VARCHAR(20) NOT NULL,
    card_front_img VARCHAR(255),
    card_back_img VARCHAR(255),
    status TINYINT DEFAULT 0 COMMENT 'Status: 0-Pending, 1-Approved, 2-Rejected',
    audit_remark VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    audit_time DATETIME,
    FOREIGN KEY (user_id) REFERENCES sys_user(id) ON DELETE CASCADE
) COMMENT 'Real-name Authentication';

-- Device Category Table (Simplified: Flat Structure)
CREATE TABLE IF NOT EXISTS device_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Device Category';

-- Device Table (Removed category_id)
CREATE TABLE IF NOT EXISTS device (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT COMMENT 'Merchant ID',
    name VARCHAR(100) NOT NULL,
    brand VARCHAR(50),
    model VARCHAR(50),
    description TEXT,
    main_image VARCHAR(255),
    detail_images TEXT COMMENT 'JSON Array of image URLs',
    specs TEXT COMMENT 'JSON Object of specifications',
    deposit_amount DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'Deposit',
    rental_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'Daily Rental Price',
    stock_quantity INT DEFAULT 0,
    status TINYINT DEFAULT 1 COMMENT 'Status: 1-On Shelf, 0-Off Shelf',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT 'Device Information';

-- Device-Category Mapping Table (Many-to-Many)
CREATE TABLE IF NOT EXISTS device_category_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (device_id) REFERENCES device(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES device_category(id) ON DELETE CASCADE,
    UNIQUE KEY uk_device_category (device_id, category_id)
) COMMENT 'Device Category Relation';

-- Rental Order Table
CREATE TABLE IF NOT EXISTS rental_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    device_id BIGINT NOT NULL,
    merchant_id BIGINT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL COMMENT 'Total Rental Fee',
    deposit_amount DECIMAL(10, 2) NOT NULL COMMENT 'Deposit Amount',
    insurance_amount DECIMAL(10, 2) DEFAULT 0.00,
    status TINYINT DEFAULT 0 COMMENT '0-Pending Pay, 1-Pending Delivery, 2-Renting, 3-Pending Return, 4-Finished, 5-Cancelled',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    pay_time DATETIME,
    delivery_time DATETIME,
    return_time DATETIME
) COMMENT 'Rental Order';

-- Repair/Complaint Table
CREATE TABLE IF NOT EXISTS after_sales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type TINYINT NOT NULL COMMENT '1-Repair, 2-Complaint',
    description TEXT,
    images TEXT,
    status TINYINT DEFAULT 0 COMMENT '0-Pending, 1-Processing, 2-Resolved',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'After Sales';

-- Device Review Table
CREATE TABLE IF NOT EXISTS rental_comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(50),
    device_id BIGINT NOT NULL,
    rating INT DEFAULT 5,
    content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT 'Device Reviews';

-- Initial Admin User (password: 123456)
INSERT INTO sys_user (username, password, nickname, role) 
SELECT 'admin', 'e10adc3949ba59abbe56e057f20f883e', 'Administrator', 'ADMIN' 
WHERE NOT EXISTS (SELECT * FROM sys_user WHERE username = 'admin');
