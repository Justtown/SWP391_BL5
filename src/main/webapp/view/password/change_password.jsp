<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    // Check if user is logged in
    Integer userId = (Integer) session.getAttribute("userId");
    if (userId == null) {
        response.sendRedirect(request.getContextPath() + "/login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đổi mật khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        .change-password-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 8px 24px rgba(15, 23, 42, 0.08);
        }
        .card-header {
            background: #ffffff;
            color: #0f172a;
            border-bottom: 1px solid rgba(15, 23, 42, 0.08);
            font-weight: 700;
        }
        .form-label {
            font-weight: 500;
            color: #495057;
            margin-bottom: 8px;
        }
        .form-control:focus {
            border-color: #80bdff;
            box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
        }
        .hint {
            font-size: 0.875rem;
            color: #6c757d;
        }
        .header-subtitle {
            font-size: 0.9rem;
            color: #64748b;
            font-weight: 500;
        }
    </style>
</head>
<body class="bg-light">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card change-password-card" style="max-width: 450px; width: 100%;">
        <div class="card-header text-center py-3">
            <h5 class="mb-1">Đổi mật khẩu</h5>
            <div class="header-subtitle">Cập nhật mật khẩu để bảo mật tài khoản</div>
        </div>
        <div class="card-body p-4">

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <%= request.getAttribute("error") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>
            
            <% if (request.getAttribute("warning") != null) { %>
            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                <%= request.getAttribute("warning") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>
            
            <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <%= request.getAttribute("message") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>
            
            <% 
            boolean mustChangePassword = request.getAttribute("mustChangePassword") != null 
                    && (Boolean) request.getAttribute("mustChangePassword");
            %>
            
            <% if (mustChangePassword) { %>
            <div class="alert alert-warning mb-3">
                Bạn phải đổi mật khẩu để tiếp tục sử dụng hệ thống.
            </div>
            <% } %>

            <form action="${pageContext.request.contextPath}/change-password" method="post">
                <div class="mb-3">
                    <label for="oldPassword" class="form-label">Mật khẩu hiện tại</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-lock"></i></span>
                        <input type="password" class="form-control" id="oldPassword" name="oldPassword" required autocomplete="current-password">
                        <button class="btn btn-outline-secondary" type="button" data-toggle-password="oldPassword" aria-label="Hiện/ẩn mật khẩu hiện tại">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="mb-3">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-key"></i></span>
                        <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="6" autocomplete="new-password">
                        <button class="btn btn-outline-secondary" type="button" data-toggle-password="newPassword" aria-label="Hiện/ẩn mật khẩu mới">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                    <div class="hint mt-1">Tối thiểu 6 ký tự</div>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="fas fa-check"></i></span>
                        <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required minlength="6" autocomplete="new-password">
                        <button class="btn btn-outline-secondary" type="button" data-toggle-password="confirmPassword" aria-label="Hiện/ẩn xác nhận mật khẩu">
                            <i class="fas fa-eye"></i>
                        </button>
                    </div>
                </div>

                <div class="d-grid mb-2">
                    <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                </div>
            </form>

            <% if (!mustChangePassword) { %>
            <div class="text-center mt-3">
                <c:choose>
                    <c:when test="${sessionScope.roleName == 'admin'}">
                        <a class="text-decoration-none" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'manager'}">
                        <a class="text-decoration-none" href="${pageContext.request.contextPath}/manager/dashboard">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'sale'}">
                        <a class="text-decoration-none" href="${pageContext.request.contextPath}/sale/dashboard">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại Dashboard
                        </a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'customer'}">
                        <a class="text-decoration-none" href="${pageContext.request.contextPath}/customer/dashboard">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại Dashboard
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a class="text-decoration-none" href="${pageContext.request.contextPath}/dashboard">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại Dashboard
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Toggle show/hide password fields
    document.querySelectorAll('[data-toggle-password]').forEach(function(btn) {
        btn.addEventListener('click', function() {
            const inputId = btn.getAttribute('data-toggle-password');
            const input = document.getElementById(inputId);
            if (!input) return;
            const icon = btn.querySelector('i');
            const isHidden = input.getAttribute('type') === 'password';
            input.setAttribute('type', isHidden ? 'text' : 'password');
            if (icon) {
                icon.classList.toggle('fa-eye', !isHidden);
                icon.classList.toggle('fa-eye-slash', isHidden);
            }
        });
    });
</script>
</body>
</html>
