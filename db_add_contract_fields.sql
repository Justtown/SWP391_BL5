-- Script để thêm các trường mới vào bảng contracts
-- Chạy script này nếu các cột chưa tồn tại trong database

USE argo_managerment_system;

-- Thêm các cột mới vào bảng contracts (nếu chưa tồn tại)
ALTER TABLE contracts 
ADD COLUMN IF NOT EXISTS customer_name VARCHAR(255) AFTER customer_id,
ADD COLUMN IF NOT EXISTS customer_phone VARCHAR(20) AFTER customer_name,
ADD COLUMN IF NOT EXISTS customer_address TEXT AFTER customer_phone,
ADD COLUMN IF NOT EXISTS machine_type_id INT AFTER customer_address,
ADD COLUMN IF NOT EXISTS quantity INT AFTER machine_type_id,
ADD COLUMN IF NOT EXISTS total_cost DECIMAL(15,2) AFTER quantity;

-- Thêm foreign key cho machine_type_id (nếu bảng machine_types tồn tại)
-- ALTER TABLE contracts ADD FOREIGN KEY (machine_type_id) REFERENCES machine_types(id);

-- Nếu MySQL không hỗ trợ IF NOT EXISTS, dùng cách này:
-- ALTER TABLE contracts ADD COLUMN customer_name VARCHAR(255);
-- ALTER TABLE contracts ADD COLUMN customer_phone VARCHAR(20);
-- ALTER TABLE contracts ADD COLUMN customer_address TEXT;
-- ALTER TABLE contracts ADD COLUMN machine_type_id INT;
-- ALTER TABLE contracts ADD COLUMN quantity INT;
-- ALTER TABLE contracts ADD COLUMN total_cost DECIMAL(15,2);
