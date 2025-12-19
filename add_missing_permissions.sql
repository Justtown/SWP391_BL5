-- Thêm permissions cho các trang bị lỗi truy cập

-- Bước 1: Thêm permissions mới vào bảng permissions
INSERT INTO permissions (permission_name, url_pattern, description) VALUES
('Customer Products', '/customer/products', 'Xem danh sách sản phẩm - Customer'),
('Customer Machines', '/customer/machines', 'Xem danh sách máy móc - Customer'),
('Sale Contracts', '/sale/contracts', 'Quản lý hợp đồng - Sale'),
('Sale Products', '/sale/products', 'Xem sản phẩm - Sale'),
('Manager Products', '/manager/products', 'Quản lý sản phẩm - Manager');

