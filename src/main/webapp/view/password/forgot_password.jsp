<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quên Mật Khẩu</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container d-flex align-items-center justify-content-center min-vh-100">
    <div class="card shadow-sm" style="max-width: 400px; width: 100%;">
        <div class="card-header bg-primary text-white text-center">
            <h5 class="mb-0">Quên Mật Khẩu</h5>
        </div>
        <div class="card-body">

            <% if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger py-2"><%= request.getAttribute("error") %></div>
            <% } %>
            
            <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success py-2"><%= request.getAttribute("message") %></div>
            <% } %>

            <form action="${pageContext.request.contextPath}/forgot-password" method="post">
                <div class="mb-3">
                    <label for="email" class="form-label">Email đăng ký</label>
                    <input type="email" class="form-control" id="email" name="email" 
                           placeholder="Nhập email của bạn" required>
                </div>

                <div class="d-grid mb-2">
                    <button type="submit" class="btn btn-primary">Tiếp tục</button>
                </div>
            </form>

            <div class="text-center mt-3">
                <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>