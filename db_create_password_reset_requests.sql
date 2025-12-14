-- Tạo bảng password_reset_requests
CREATE TABLE IF NOT EXISTS password_reset_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    email VARCHAR(255) NOT NULL,
    request_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'pending', -- pending, approved, rejected
    new_password VARCHAR(255), -- Store new password temporarily if approved (plain text for email)
    password_changed BOOLEAN DEFAULT FALSE, -- Đánh dấu user đã đổi mật khẩu chưa
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

