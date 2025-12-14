-- Thêm column approved_time vào bảng password_reset_requests
ALTER TABLE password_reset_requests 
ADD COLUMN approved_time TIMESTAMP NULL;

