-- ============================================
-- THÊM CỘT SALE_ID VÀO BẢNG CONTRACTS
-- ============================================

USE argo_managerment_system;

-- Kiểm tra và thêm cột sale_id nếu chưa có
SET @dbname = DATABASE();
SET @tablename = "contracts";
SET @columnname = "sale_id";
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (column_name = @columnname)
  ) > 0,
  "SELECT 'Column sale_id already exists.'",
  CONCAT("ALTER TABLE ", @tablename, " ADD COLUMN ", @columnname, " INT NULL AFTER manager_id")
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- Kiểm tra kết quả
SELECT 'Column sale_id added successfully!' as Result;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'argo_managerment_system' 
  AND TABLE_NAME = 'contracts' 
  AND COLUMN_NAME = 'sale_id';
