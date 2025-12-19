-- Thêm cột quantity vào bảng service_orders
USE argo_management_system;

ALTER TABLE service_orders 
ADD COLUMN quantity INT DEFAULT 1 AFTER machine_id;

-- Cập nhật dữ liệu cũ để có quantity = 1
UPDATE service_orders SET quantity = 1 WHERE quantity IS NULL;
