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
    <style>
        body {
            background-color: #f8f9fa;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        .change-password-card {
            border: none;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        .card-header {
            background-color: #495057;
            color: white;
            border: none;
            font-weight: 500;
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
    </style>
</head>
<body class="bg-light">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card change-password-card" style="max-width: 450px; width: 100%;">
        <div class="card-header text-center py-3">
            <h5 class="mb-0">Đổi mật khẩu</h5>
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
                    <input type="password" class="form-control" id="oldPassword" name="oldPassword" required>
                </div>

                <div class="mb-3">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" required minlength="6">
                    <small class="text-muted">Tối thiểu 6 ký tự</small>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu mới</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" required minlength="6">
                </div>

                <div class="d-grid mb-2">
                    <button type="submit" class="btn btn-primary">Đổi mật khẩu</button>
                </div>
            </form>

            <% if (!mustChangePassword) { %>
            <div class="text-center mt-3">
                <c:choose>
                    <c:when test="${sessionScope.roleName == 'admin'}">
                        <a href="${pageContext.request.contextPath}/admin/dashboard">Quay lại Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'manager'}">
                        <a href="${pageContext.request.contextPath}/manager/dashboard">Quay lại Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'sale'}">
                        <a href="${pageContext.request.contextPath}/sale/dashboard">Quay lại Dashboard</a>
                    </c:when>
                    <c:when test="${sessionScope.roleName == 'customer'}">
                        <a href="${pageContext.request.contextPath}/customer/dashboard">Quay lại Dashboard</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/dashboard">Quay lại Dashboard</a>
                    </c:otherwise>
                </c:choose>
            </div>
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
