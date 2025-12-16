<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!-- Sidebar -->
<div class="sidebar bg-dark text-white" id="sidebar">
    <div class="sidebar-header p-3 border-bottom border-secondary">
        <h5 class="mb-0">
            <i class="fas fa-leaf me-2"></i>Argo Machine
        </h5>
    </div>

    <nav class="sidebar-nav">
        <ul class="nav flex-column">

            <!-- Dashboard - Hiển thị cho tất cả role -->
            <li class="nav-item">
                <c:choose>
                    <c:when test="${sessionScope.roleName == 'admin'}">
                        <a class="nav-link text-white" href="${ctx}/admin/dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'manager'}">
                        <a class="nav-link text-white" href="${ctx}/manager/dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'sale'}">
                        <a class="nav-link text-white" href="${ctx}/sale/dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'customer'}">
                        <a class="nav-link text-white" href="${ctx}/customer/dashboard">
                            <i class="fas fa-tachometer-alt me-2"></i>Dashboard
                        </a>
                    </c:when>
                </c:choose>
            </li>

            <!-- ==================== ADMIN MENU ==================== -->
            <c:if test="${sessionScope.roleName == 'admin'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Quản trị hệ thống</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/manage-account">
                    <a class="nav-link text-white" href="${ctx}/admin/statistics">
                        <i class="fas fa-chart-bar me-2"></i>Thống kê
                    </a>
                </li>
                <li class="nav-item">

                    <a class="nav-link text-white" href="${ctx}/admin/manage-account">
                        <i class="fas fa-users me-2"></i>Quản lý User
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/admin/roles">
                        <i class="fas fa-user-shield me-2"></i>Quản lý Permission
                    </a>
                </li>
            </c:if>

            <!-- ==================== MANAGER MENU ==================== -->
            <c:if test="${sessionScope.roleName == 'manager'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Quản lý nghiệp vụ</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/manager/machines">
                        <i class="fas fa-cogs me-2"></i>Quản lý Machine
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/manager/contracts">
                        <i class="fas fa-file-contract me-2"></i>Quản lý Contract
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/manager/products">
                        <i class="fas fa-box me-2"></i>Quản lý Product
                    </a>
                </li>
            </c:if>

            <!-- ==================== SALE MENU ==================== -->
            <c:if test="${sessionScope.roleName == 'sale'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Kinh doanh</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/sale/contracts">
                        <i class="fas fa-file-contract me-2"></i>Xem Contract
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/sale/products">
                        <i class="fas fa-box me-2"></i>Xem Product
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/sale/orders">
                        <i class="fas fa-shopping-cart me-2"></i>Tạo đơn hàng
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/requests">
                        <i class="fas fa-clipboard-check me-2"></i>Quản lý Request
                    </a>
                </li>
            </c:if>

            <!-- ==================== CUSTOMER MENU ==================== -->
            <c:if test="${sessionScope.roleName == 'customer'}">
                <li class="nav-item">
                    <div class="nav-link text-secondary small text-uppercase mt-3">
                        <span>Dịch vụ</span>
                    </div>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/customer/products">
                        <i class="fas fa-box me-2"></i>Xem Product
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/customer/contracts">
                        <i class="fas fa-file-contract me-2"></i>Contract của tôi
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link text-white" href="${ctx}/requests">
                        <i class="fas fa-paper-plane me-2"></i>Request của tôi
                    </a>
                </li>
            </c:if>
            <!-- ==================== COMMON MENU ==================== -->
            <li class="nav-item">
                <div class="nav-link text-secondary small text-uppercase mt-3">
                    <span>Tài khoản</span>
                </div>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="${ctx}/profile">
                    <i class="fas fa-user me-2"></i>Hồ sơ cá nhân
                </a>
            </li>
            <li class="nav-item">
                <a class="nav-link text-white" href="${ctx}/change-password">
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

<!-- Logout Modal -->
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