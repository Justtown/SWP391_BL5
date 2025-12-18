-- CHẠY TỪNG BƯỚC MỘT, KIỂM TRA KẾT QUẢ SAU MỖI BƯỚC

-- BƯỚC 1: Kiểm tra xem permission đã tồn tại chưa
SELECT 'Kiểm tra trước khi thêm:' AS Note;
SELECT * FROM argo_management_system.permissions WHERE permission_name = 'manager.orders';

-- BƯỚC 2: Thêm permission mới (nếu chưa có)
INSERT INTO argo_management_system.permissions (permission_name, description, url_pattern, created_at)
SELECT 'manager.orders', 'Quản lý Order', '/manager/orders', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM argo_management_system.permissions WHERE permission_name = 'manager.orders'
);

-- BƯỚC 3: Kiểm tra đã thêm chưa
SELECT 'Sau khi thêm permission:' AS Note;
SELECT * FROM argo_management_system.permissions WHERE permission_name = 'manager.orders';

-- BƯỚC 4: Lấy ID của permission và role
SELECT 'Permission ID:' AS Note, id FROM argo_management_system.permissions WHERE permission_name = 'manager.orders';
SELECT 'Manager Role ID:' AS Note, id FROM argo_management_system.roles WHERE role_name = 'manager';

-- BƯỚC 5: Gán permission cho role manager
INSERT INTO argo_management_system.role_permission (role_id, permission_id, created_at)
SELECT 
    (SELECT id FROM argo_management_system.roles WHERE role_name = 'manager'),
    (SELECT id FROM argo_management_system.permissions WHERE permission_name = 'manager.orders'),
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM argo_management_system.role_permission rp
    WHERE rp.role_id = (SELECT id FROM argo_management_system.roles WHERE role_name = 'manager')
    AND rp.permission_id = (SELECT id FROM argo_management_system.permissions WHERE permission_name = 'manager.orders')
);

-- BƯỚC 6: Kiểm tra kết quả cuối cùng
SELECT 'KẾT QUẢ CUỐI CÙNG:' AS Note;
SELECT rp.id, r.role_name, p.permission_name, p.url_pattern, rp.created_at
FROM argo_management_system.role_permission rp
JOIN argo_management_system.roles r ON rp.role_id = r.id
JOIN argo_management_system.permissions p ON rp.permission_id = p.id
WHERE p.permission_name = 'manager.orders';
