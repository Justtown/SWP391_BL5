create database argo_managerment_system;
use argo_managerment_system;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    email VARCHAR(255),
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);
CREATE TABLE permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255)
);
CREATE TABLE user_role (
    user_id INT,
    role_id INT,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE TABLE role_permission (
    role_id INT,
    permission_id INT,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id)
);

INSERT INTO roles (role_name, description) VALUES
('admin', 'Full system control'),
('manager', 'Manage employees and operations'),
('sale', 'Sales operations'),
('customer', 'Regular user of the system');
INSERT INTO permissions (permission_name, description) VALUES
('user.view', 'View users'),
('user.create', 'Create users'),
('user.update', 'Update users'),
('user.delete', 'Delete users'),

('product.view', 'View product list'),
('product.create', 'Create product'),
('product.update', 'Update product'),
('product.delete', 'Delete product'),

('order.view', 'View orders'),
('order.create', 'Create new order'),
('order.update', 'Update order status'),
('order.delete', 'Delete order');
INSERT INTO role_permission (role_id, permission_id)
SELECT 1 AS role_id, id AS permission_id FROM permissions;
INSERT INTO role_permission (role_id, permission_id)
SELECT 3, id FROM permissions
WHERE permission_name IN (
    'product.view',
    'order.view',
    'order.create',
    'order.update'
);
INSERT INTO role_permission (role_id, permission_id)
SELECT 4, id FROM permissions
WHERE permission_name IN (
    'product.view',
    'order.create',
    'order.view'
);

-- Thêm dữ liệu mẫu cho bảng users
-- Password cho tất cả user là: 123456 (đã hash MD5: e10adc3949ba59abbe56e057f20f883e)
INSERT INTO users (username, password, full_name, email, status) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', 'Administrator', 'admin@gmail.com', 1),
('manager1', 'e10adc3949ba59abbe56e057f20f883e', 'Manager One', 'manager1@gmail.com', 1),
('sale1', 'e10adc3949ba59abbe56e057f20f883e', 'Sale One', 'sale1@gmail.com', 1),
('customer1', 'e10adc3949ba59abbe56e057f20f883e', 'Customer One', 'customer1@gmail.com', 1),
('testuser', 'e10adc3949ba59abbe56e057f20f883e', 'Test User', 'testuser@gmail.com', 1);

-- Gán role cho users
INSERT INTO user_role (user_id, role_id) VALUES
(1, 1), -- admin có role admin
(2, 2), -- manager1 có role manager
(3, 3), -- sale1 có role sale
(4, 4), -- customer1 có role customer
(5, 4); -- testuser có role customer