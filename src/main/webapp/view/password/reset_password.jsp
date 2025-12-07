<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    // Check if user has valid reset session
    Integer resetUserId = (Integer) session.getAttribute("resetUserId");
    if (resetUserId == null) {
        response.sendRedirect(request.getContextPath() + "/forgot-password");
        return;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-sm" style="max-width: 400px; width: 100%;">
        <div class="card-header bg-primary text-white text-center">
            <h5 class="mb-0">Đặt Lại Mật Khẩu</h5>
        </div>
        <div class="card-body">

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2"><%= request.getAttribute("error") %></div>
            <% } %>
            
            <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success py-2"><%= request.getAttribute("message") %></div>
            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập ngay</a>
            </div>
            <% } else { %>

            <form action="${pageContext.request.contextPath}/reset-password" method="post">
                <div class="mb-3">
                    <label for="newPassword" class="form-label">Mật khẩu mới</label>
                    <input type="password" class="form-control" id="newPassword" name="newPassword" 
                           placeholder="Nhập mật khẩu mới" required minlength="6">
                    <small class="text-muted">Tối thiểu 6 ký tự</small>
                </div>

                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Xác nhận mật khẩu</label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                           placeholder="Nhập lại mật khẩu" required minlength="6">
                </div>

                <div class="d-grid mb-2">
                    <button type="submit" class="btn btn-primary">Đặt lại mật khẩu</button>
                </div>
            </form>

            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
            </div>
            
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>