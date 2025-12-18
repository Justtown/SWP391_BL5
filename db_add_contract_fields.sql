-- Script để thêm các trường mới vào bảng contracts
-- Chạy script này nếu các cột chưa tồn tại trong database

USE argo_management_system;

-- MySQL compatibility script (chạy nhiều lần không lỗi)
-- Lưu ý: DB của project đang dùng argo_management_system (khác với file db.sql cũ).

SET @db := DATABASE();

-- Helper: add column if missing via INFORMATION_SCHEMA + dynamic SQL
SET @sql := NULL;

-- customer_name
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN customer_name VARCHAR(255) NULL AFTER customer_id',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'customer_name';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- customer_phone
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN customer_phone VARCHAR(20) NULL AFTER customer_name',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'customer_phone';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- customer_address
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN customer_address TEXT NULL AFTER customer_phone',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'customer_address';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- machine_id
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN machine_id INT NULL AFTER customer_address',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'machine_id';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- machine_type_id (optional; already exists on many DBs)
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN machine_type_id INT NULL AFTER machine_id',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'machine_type_id';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- quantity
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN quantity INT NULL AFTER machine_type_id',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'quantity';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- total_cost
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN total_cost DECIMAL(15,2) NULL AFTER quantity',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'total_cost';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- service_description
SELECT IF(COUNT(*) = 0,
          'ALTER TABLE contracts ADD COLUMN service_description TEXT NULL AFTER total_cost',
          NULL)
INTO @sql
FROM information_schema.columns
WHERE table_schema = @db AND table_name = 'contracts' AND column_name = 'service_description';
SET @sql = IFNULL(@sql, 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- (Optional) FK for machine_id:
-- ALTER TABLE contracts ADD CONSTRAINT fk_contract_machine FOREIGN KEY (machine_id) REFERENCES machines(id);
