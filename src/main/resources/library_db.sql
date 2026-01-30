-- 创建数据库
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 图书表
CREATE TABLE books (
    id INT PRIMARY KEY AUTO_INCREMENT,
    isbn VARCHAR(13) UNIQUE NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(100) NOT NULL,
    publisher VARCHAR(100),
    publish_year YEAR,
    category VARCHAR(50),
    total_copies INT DEFAULT 1,
    available_copies INT DEFAULT 1,
    price DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 读者表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    card_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    address TEXT,
    registration_date DATE NOT NULL,
    max_borrow_limit INT DEFAULT 5,
    current_borrow_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 借阅记录表
CREATE TABLE borrow_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    book_id INT NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    status ENUM('BORROWED', 'RETURNED', 'OVERDUE') DEFAULT 'BORROWED',
    fine_amount DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);

-- 插入测试数据
INSERT INTO books (isbn, title, author, publisher, publish_year, category, total_copies, available_copies, price) VALUES
('9787120000001', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', 2017, '计算机', 5, 5, 108.00),
('9787120000002', '深入理解计算机系统', 'Randal Bryant', '机械工业出版社', 2016, '计算机', 3, 3, 139.00),
('9787020000003', '三体', '刘慈欣', '重庆出版社', 2008, '科幻小说', 10, 10, 23.00),
('9787540000004', '活着', '余华', '作家出版社', 2012, '文学', 8, 8, 28.00);

INSERT INTO users (card_number, name, email, phone, address, registration_date, max_borrow_limit) VALUES
('20230001', '张三', 'zhangsan@email.com', '13800138001', '北京市海淀区', '2023-01-15', 5),
('20230002', '李四', 'lisi@email.com', '13800138002', '上海市浦东新区', '2023-02-20', 5),
('20230003', '王五', 'wangwu@email.com', '13800138003', '广州市天河区', '2023-03-10', 3);