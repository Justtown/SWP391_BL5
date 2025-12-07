-- ============================================
-- TẠO BẢNG PROFILE CHO CHỨC NĂNG MY PROFILE
-- ============================================

USE argo_managerment_system;

-- Tạo bảng profiles
CREATE TABLE profiles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NULL,
    address VARCHAR(500) NULL,
    avatar VARCHAR(500) NULL,
    birthdate DATE NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id)
);

-- Thêm dữ liệu mẫu cho profiles (tương ứng với users đã có)
INSERT INTO profiles (user_id, name, email, phone, address, birthdate) VALUES
(1, 'Administrator', 'admin@gmail.com', '0123456789', '123 Admin Street', '1990-01-01'),
(2, 'Manager One', 'manager1@gmail.com', '0987654321', '456 Manager Avenue', '1985-05-15'),
(3, 'Sale One', 'sale1@gmail.com', '0111222333', '789 Sale Road', '1992-08-20'),
(4, 'Customer One', 'customer1@gmail.com', '0444555666', '321 Customer Lane', '1995-12-10'),
(5, 'Test User', 'testuser@gmail.com', '0777888999', '654 Test Boulevard', '1998-03-25');

-- Kiểm tra kết quả
-- SELECT p.*, u.username, r.role_name 
-- FROM profiles p
-- JOIN users u ON p.user_id = u.id
-- LEFT JOIN user_role ur ON u.id = ur.user_id
-- LEFT JOIN roles r ON ur.role_id = r.id;

