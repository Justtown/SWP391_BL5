-- Create service_orders table for managing orders from sale role
-- Orders need to be approved by admin before processing

USE argo_managerment_system;

DROP TABLE IF EXISTS service_orders;

CREATE TABLE service_orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    contract_code VARCHAR(100) NOT NULL UNIQUE,
    customer_name VARCHAR(255) NOT NULL,
    customer_phone VARCHAR(20),
    customer_address TEXT,
    machine_id INT,
    service_description TEXT,
    start_date DATE,
    end_date DATE,
    status VARCHAR(50) DEFAULT 'PENDING',
    total_cost DECIMAL(15, 2),
    created_by INT NOT NULL,
    approved_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_status (status),
    INDEX idx_created_by (created_by)
);

-- Status values:
-- PENDING: Waiting for admin approval (created by sale)
-- APPROVED: Approved by admin
-- REJECTED: Rejected by admin
-- IN_PROGRESS: Order is being processed
-- COMPLETED: Order completed
-- CANCELLED: Order cancelled

-- Insert sample data
INSERT INTO service_orders 
(contract_code, customer_name, customer_phone, customer_address, service_description, 
 start_date, end_date, status, total_cost, created_by) 
VALUES
('HD001', 'Công ty ABC', '0901234567', '123 Đường ABC, Q1, HCM', 
 'Gia công chi tiết máy móc', '2025-01-01', '2025-01-31', 'PENDING', 50000000, 3),
('HD002', 'Công ty XYZ', '0902345678', '456 Đường XYZ, Q2, HCM', 
 'Sửa chữa và bảo trì máy', '2025-02-01', '2025-02-15', 'APPROVED', 30000000, 3);
