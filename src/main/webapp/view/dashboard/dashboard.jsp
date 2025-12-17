<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle} - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: #f8f9fa;
        }
        
        .dashboard-header {
            background: white;
            border-bottom: 1px solid #dee2e6;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .dashboard-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 1.5rem;
            margin-bottom: 1.5rem;
            transition: box-shadow 0.2s;
        }
        
        .dashboard-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
        
        .stat-card {
            text-align: center;
            padding: 1.5rem;
        }
        
        .stat-card .stat-icon {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: #0d6efd;
        }
        
        .stat-card .stat-value {
            font-size: 2rem;
            font-weight: 600;
            color: #212529;
        }
        
        .stat-card .stat-label {
            color: #6c757d;
            font-size: 0.9rem;
        }
        
        .quick-action {
            display: block;
            padding: 1rem;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            text-decoration: none;
            color: #212529;
            transition: all 0.2s;
            margin-bottom: 0.75rem;
        }
        
        .quick-action:hover {
            background-color: #f8f9fa;
            border-color: #0d6efd;
            color: #0d6efd;
            text-decoration: none;
        }
        
        .quick-action i {
            margin-right: 0.5rem;
        }
        
        .role-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
        }
        
        .role-admin { background-color: #dc3545; color: white; }
        .role-manager { background-color: #0d6efd; color: white; }
        .role-sale { background-color: #198754; color: white; }
        .role-customer { background-color: #6c757d; color: white; }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Dashboard Header -->
        <div class="dashboard-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1">${pageTitle}</h4>
                <span class="role-badge role-${sessionScope.roleName}">${sessionScope.roleName}</span>
            </div>
            <div class="d-flex align-items-center">
                <span class="me-3">
                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
                </span>
                <a href="#" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#logoutModal">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                </a>
            </div>
        </div>
        
        <!-- Welcome Message -->
        <div class="container-fluid">
            <div class="dashboard-card">
                <h5 class="mb-3">
                    <i class="fas fa-hand-wave me-2"></i>${welcomeMessage}
                </h5>
                <p class="text-muted mb-0">
                    Bạn đang đăng nhập với vai trò <strong>${sessionScope.roleName}</strong>. 
                    Sử dụng menu bên trái để điều hướng đến các chức năng của bạn.
                </p>
            </div>
            
            <!-- Quick Actions based on Role -->
            <div class="row">
                <div class="col-lg-8">
                    <div class="dashboard-card">
                        <h6 class="mb-3"><i class="fas fa-bolt me-2"></i>Thao tác nhanh</h6>
                        <div class="row">
                            
                            <!-- Admin Quick Actions -->
                            <c:if test="${sessionScope.roleName == 'admin'}">
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/admin/manage-account" class="quick-action">
                                        <i class="fas fa-users"></i> Quản lý người dùng
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/admin/roles" class="quick-action">
                                        <i class="fas fa-user-shield"></i> Quản lý phân quyền
                                    </a>
                                </div>
                            </c:if>
                            
                            <!-- Manager Quick Actions -->
                            <c:if test="${sessionScope.roleName == 'manager'}">
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/manager/machines" class="quick-action">
                                        <i class="fas fa-cogs"></i> Quản lý Machine
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/manager/contracts" class="quick-action">
                                        <i class="fas fa-file-contract"></i> Quản lý Contract
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/manager/products" class="quick-action">
                                        <i class="fas fa-box"></i> Quản lý Product
                                    </a>
                                </div>
                            </c:if>
                            
                            <!-- Sale Quick Actions -->
                            <c:if test="${sessionScope.roleName == 'sale'}">
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/sale/contracts" class="quick-action">
                                        <i class="fas fa-file-contract"></i> Xem Contract
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/sale/products" class="quick-action">
                                        <i class="fas fa-box"></i> Xem Product
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/sale/orders?action=list" class="quick-action">
                                        <i class="fas fa-shopping-cart"></i> Quản lý đơn hàng
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/sale/orders?action=create" class="quick-action">
                                        <i class="fas fa-plus-circle"></i> Tạo đơn hàng mới
                                    </a>
                                </div>
                            </c:if>
                            
                            <!-- Customer Quick Actions -->
                            <c:if test="${sessionScope.roleName == 'customer'}">
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/customer/products" class="quick-action">
                                        <i class="fas fa-box"></i> Xem sản phẩm
                                    </a>
                                </div>
                                <div class="col-md-6">
                                    <a href="${pageContext.request.contextPath}/customer/contracts" class="quick-action">
                                        <i class="fas fa-file-contract"></i> Hợp đồng của tôi
                                    </a>
                                </div>
                            </c:if>
                            
                            <!-- Common Actions -->
                            <div class="col-md-6">
                                <a href="${pageContext.request.contextPath}/profile" class="quick-action">
                                    <i class="fas fa-user"></i> Xem hồ sơ cá nhân
                                </a>
                            </div>
                            <div class="col-md-6">
                                <a href="${pageContext.request.contextPath}/change-password" class="quick-action">
                                    <i class="fas fa-key"></i> Đổi mật khẩu
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- User Info Card -->
                <div class="col-lg-4">
                    <div class="dashboard-card">
                        <h6 class="mb-3"><i class="fas fa-info-circle me-2"></i>Thông tin tài khoản</h6>
                        <div class="mb-2">
                            <small class="text-muted">Họ và tên</small>
                            <div>${sessionScope.fullName}</div>
                        </div>
                        <div class="mb-2">
                            <small class="text-muted">Tên đăng nhập</small>
                            <div>${sessionScope.username}</div>
                        </div>
                        <div class="mb-2">
                            <small class="text-muted">Vai trò</small>
                            <div>
                                <span class="role-badge role-${sessionScope.roleName}">${sessionScope.roleName}</span>
                            </div>
                        </div>
                        <hr>
                        <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-primary btn-sm w-100">
                            <i class="fas fa-edit me-1"></i> Chỉnh sửa hồ sơ
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
