<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        .dashboard-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        .page-title {
            font-size: 1.75rem;
            font-weight: 600;
            margin-bottom: 30px;
            color: #212529;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 20px;
            transition: box-shadow 0.2s;
        }
        .stat-card:hover {
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
        .stat-card-title {
            font-size: 0.9rem;
            color: #6c757d;
            margin-bottom: 10px;
            font-weight: 500;
        }
        .stat-card-value {
            font-size: 2rem;
            font-weight: 600;
            color: #212529;
        }
        .menu-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        .menu-item {
            display: block;
            background: white;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            padding: 20px;
            text-decoration: none;
            color: #212529;
            transition: all 0.2s;
        }
        .menu-item:hover {
            background: #f8f9fa;
            border-color: #0d6efd;
            color: #0d6efd;
            text-decoration: none;
        }
        .menu-item-title {
            font-weight: 500;
            margin-bottom: 5px;
        }
        .menu-item-desc {
            font-size: 0.85rem;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <div class="container py-4">
        <div class="dashboard-container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="page-title mb-0">Admin Dashboard</h1>
                <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-secondary btn-sm">
                    Về trang chủ
                </a>
            </div>
            
            <%-- Statistics Cards --%>
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-card-title">Tổng số người dùng</div>
                    <div class="stat-card-value">${totalUsers}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-title">Yêu cầu đang chờ</div>
                    <div class="stat-card-value">${totalPendingRequests}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-card-title">Yêu cầu đã duyệt</div>
                    <div class="stat-card-value">${totalApprovedRequests}</div>
                </div>
            </div>
            
            <%-- Menu Items --%>
            <h5 class="mb-3" style="color: #495057; font-weight: 600;">Quản lý hệ thống</h5>
            <div class="menu-grid">
                <a href="${pageContext.request.contextPath}/admin/manage-account" class="menu-item">
                    <div class="menu-item-title">Quản lý người dùng</div>
                    <div class="menu-item-desc">Xem và quản lý tất cả người dùng trong hệ thống</div>
                </a>
                <a href="${pageContext.request.contextPath}/admin/password-reset-requests" class="menu-item">
                    <div class="menu-item-title">Yêu cầu đặt lại mật khẩu</div>
                    <div class="menu-item-desc">Duyệt và từ chối yêu cầu đặt lại mật khẩu</div>
                </a>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

