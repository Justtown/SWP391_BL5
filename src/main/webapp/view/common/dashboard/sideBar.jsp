<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="currentUri" value="${pageContext.request.requestURI}" />

<div class="sidebar bg-dark text-white" id="sidebar">
    <div class="sidebar-header p-3 border-bottom border-secondary">
        <h5 class="mb-0">
            <i class="fas fa-leaf me-2"></i>Argo Machine
        </h5>
    </div>

    <nav class="sidebar-nav">
        <ul class="nav flex-column">

            <li class="nav-item">
                <a class="nav-link text-white ${currentUri != null && currentUri.endsWith('/dashboard') ? 'active' : ''}" href="${ctx}/${sessionScope.roleName}/dashboard">
                    <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                </a>
            </li>

            <c:if test="${sessionScope.roleName == 'admin'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Quản trị hệ thống</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/admin/statistics') ? 'active' : ''}" href="${ctx}/admin/statistics">
                        <i class="fas fa-chart-bar me-2"></i>Thống kê
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/admin/manage-account') ? 'active' : ''}" href="${ctx}/admin/manage-account">
                        <i class="fas fa-users me-2"></i>Quản lý User
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/admin/password-reset-requests') ? 'active' : ''}" href="${ctx}/admin/password-reset-requests">
                        <i class="fas fa-key me-2"></i>Yêu cầu đặt lại MK
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/admin/permissions') ? 'active' : ''}" href="${ctx}/admin/permissions?action=matrix">
                        <i class="fas fa-user-shield me-2"></i>Quản lý Permission
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/admin/pending-users') ? 'active' : ''}" href="${ctx}/admin/pending-users">
                        <i class="fas fa-user-clock me-2"></i>User chờ duyệt
                    </a>
                </li>
            </c:if>

            <c:if test="${sessionScope.roleName == 'manager'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Quản lý nghiệp vụ</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/machines') ? 'active' : ''}" href="${ctx}/manager/machines">
                        <i class="fas fa-cogs me-2"></i>Quản lý Machine
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/machine-types') ? 'active' : ''}" href="${ctx}/manager/machine-types">
                        <i class="fas fa-layer-group me-2"></i>Quản lý Loại Máy
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/maintenances') ? 'active' : ''}" href="${ctx}/manager/maintenances">
                        <i class="fas fa-tools me-2"></i>Bảo Trì Máy Trong Kho
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/orders') ? 'active' : ''}" href="${ctx}/manager/orders?action=list">
                        <i class="fas fa-shopping-cart me-2"></i>Quản lý Order
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/contracts') ? 'active' : ''}" href="${ctx}/manager/contracts">
                        <i class="fas fa-file-contract me-2"></i>Quản lý Contract
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/products') ? 'active' : ''}" href="${ctx}/manager/products">
                        <i class="fas fa-box me-2"></i>Quản lý Product
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/manager/statistics') ? 'active' : ''}" href="${ctx}/manager/statistics">
                        <i class="fas fa-chart-bar me-2"></i>Thống kê
                    </a>
                </li>
            </c:if>

            <c:if test="${sessionScope.roleName == 'sale'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Kinh doanh</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/sale/contracts') ? 'active' : ''}" href="${ctx}/sale/contracts">
                        <i class="fas fa-file-contract me-2"></i>Xem Contract
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/sale/products') ? 'active' : ''}" href="${ctx}/sale/products">
                        <i class="fas fa-box me-2"></i>Xem Product
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/sale/orders') ? 'active' : ''}" href="${ctx}/sale/orders?action=list">
                        <i class="fas fa-shopping-cart me-2"></i>Quản lý đơn hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/requests') ? 'active' : ''}" href="${ctx}/requests">
                        <i class="fas fa-clipboard-check me-2"></i>Quản lý Request
                    </a>
                </li>
            </c:if>

            <c:if test="${sessionScope.roleName == 'customer'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Dịch vụ</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/customer/products') ? 'active' : ''}" href="${ctx}/customer/products">
                        <i class="fas fa-box me-2"></i>Xem Product
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/customer/contracts') ? 'active' : ''}" href="${ctx}/customer/contracts">
                        <i class="fas fa-file-contract me-2"></i>Contract của tôi
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white ${currentUri != null && currentUri.contains('/requests') ? 'active' : ''}" href="${ctx}/requests">
                        <i class="fas fa-paper-plane me-2"></i>Request của tôi
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/customer/machines">
                        <i class="fas fa-cogs me-2"></i>Danh sách Machine
                    </a>
                </li>

            </c:if>

            <li class="nav-item">
                <div class="nav-link text-secondary small text-uppercase mt-3">
                    <span>Tài khoản</span>
                </div>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white ${currentUri != null && currentUri.contains('/profile') ? 'active' : ''}" href="${ctx}/profile">
                    <i class="fas fa-user me-2"></i>Hồ sơ cá nhân
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white ${currentUri != null && currentUri.contains('/change-password') ? 'active' : ''}" href="${ctx}/change-password">
                    <i class="fas fa-key me-2"></i>Đổi mật khẩu
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="#" data-bs-toggle="modal" data-bs-target="#logoutModal">
                    <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
                </a>
            </li>
        </ul>
    </nav>
</div>

<div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content" style="border-radius: 10px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); border: none;">
            <div class="modal-body p-5 text-center">
                <div class="mb-4">
                    <i class="fas fa-sign-out-alt fa-3x text-primary"></i>
                </div>
                <h4 class="mb-3">Đăng xuất</h4>
                <p class="text-muted mb-4">Bạn có muốn đăng xuất khỏi hệ thống không?</p>
                <div class="d-flex justify-content-center gap-3">
                    <form action="${ctx}/logout" method="POST" style="display: inline;">
                        <input type="hidden" name="confirm" value="yes">
                        <button type="submit" class="btn btn-primary px-4">
                            <i class="fas fa-check me-1"></i> Có, đăng xuất
                        </button>
                    </form>
                    <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">
                        <i class="fas fa-times me-1"></i> Không
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
    .sidebar {
        width: 250px;
        min-height: 100vh;
        position: fixed;
        top: 0;
        left: 0;
        z-index: 1000;
        transition: all 0.3s;
    }

    .sidebar-nav .nav-link {
        padding: 0.75rem 1rem;
        border-radius: 0;
        transition: all 0.2s;
    }

    .sidebar-nav .nav-link:hover {
        background-color: rgba(255, 255, 255, 0.1);
        padding-left: 1.25rem;
    }

    .sidebar-nav .nav-link.active {
        background-color: rgba(255, 255, 255, 0.15);
        border-left: 3px solid #0d6efd;
    }

    .main-content {
        margin-left: 250px;
        padding: 20px;
        min-height: 100vh;
        background-color: #f8f9fa;
    }

    @media (max-width: 768px) {
        .sidebar {
            margin-left: -250px;
        }
        .sidebar.active {
            margin-left: 0;
        }
        .main-content {
            margin-left: 0;
        }
    }
</style>