<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    String mode = (String) request.getAttribute("mode");
    if (mode == null) {
        mode = "forgot";
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body class="bg-light">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-sm" style="max-width: 450px; width: 100%;">
        <div class="card-header bg-primary text-white text-center">
            <h5 class="mb-0">
                <i class="fas fa-key"></i> Quên Mật Khẩu
            </h5>
        </div>
        <div class="card-body p-4">

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="fas fa-exclamation-circle"></i> <%= request.getAttribute("error") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>
            
            <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="fas fa-check-circle"></i> <%= request.getAttribute("message") %>
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <% } %>

            <% if ("success".equals(mode)) { %>
                <!-- Success message -->
                <div class="text-center py-4">
                    <i class="fas fa-check-circle text-success" style="font-size: 3rem;"></i>
                    <h5 class="mt-3">Yêu cầu đã được gửi!</h5>
                    <p class="text-muted">Vui lòng kiểm tra email để nhận mật khẩu mới sau khi admin phê duyệt.</p>
                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary mt-3">
                        <i class="fas fa-sign-in-alt"></i> Đăng nhập
                    </a>
                </div>
            <% } else { %>
                <!-- Forgot password form -->
                <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                    <div class="mb-3">
                        <label for="email" class="form-label">
                            <i class="fas fa-envelope"></i> Email đăng ký
                        </label>
                        <input type="email" class="form-control" id="email" name="email" 
                               placeholder="Nhập email của bạn" required autofocus>
                        <small class="text-muted">Nhập email đã đăng ký trong hệ thống</small>
                    </div>

                    <div class="d-grid mb-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-paper-plane"></i> Gửi yêu cầu
                        </button>
                    </div>
                </form>

                <div class="text-center">
                    <p class="text-muted mb-2">
                        <i class="fas fa-info-circle"></i> Sau khi gửi yêu cầu, admin sẽ xem xét và gửi mật khẩu mới qua email.
                    </p>
                    <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">
                        <i class="fas fa-arrow-left"></i> Quay lại đăng nhập
                    </a>
                </div>
            <% } %>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

