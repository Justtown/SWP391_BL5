<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f5f7fa;
            text-align: center;
        }
        .logo { margin-top: 20px; }
        .title {
            font-size: 18px;
            color: #1e4e79;
            margin-top: 5px;
        }
        .menu {
            margin-top: 15px;
            background: #1e4e79;
            padding: 12px 0;
        }
        .menu a {
            color: white;
            text-decoration: none;
            margin: 0 25px;
            font-size: 15px;
            font-weight: bold;
        }
        .menu a:hover { opacity: 0.7; }
        .container-main {
            width: 90%;
            background: white;
            min-height: 450px;
            border-radius: 8px;
            padding: 25px;
            border: 1px solid #d8d8d8;
            margin: 30px auto;
        }
        h2 { color: #1e4e79; }
        .user-info {
            background: #e8f4fd;
            padding: 15px;
            border-radius: 8px;
            display: inline-block;
        }
    </style>
</head>
<body>

<%
    // Check login status
    Integer userId = (Integer) session.getAttribute("userId");
    String username = (String) session.getAttribute("username");
    String fullName = (String) session.getAttribute("fullName");
    boolean isLoggedIn = (userId != null);
%>

<div class="logo">
    <img src="images/logo.png" alt="Logo" style="height:100px;" onerror="this.style.display='none'">
</div>

<div class="title">Argo Machine Management</div>

<%
    // Lấy thông tin từ session
    boolean isAdmin = "admin".equalsIgnoreCase(username); // Nếu username là admin
%>

<div class="menu">
    <a href="${pageContext.request.contextPath}/home">Home</a>
    <a href="${pageContext.request.contextPath}/product">Product</a>
    <a href="${pageContext.request.contextPath}/introduce">Introduce</a>

    <% if (isLoggedIn) { %>
    <% if (isAdmin) { %>
    <a href="${pageContext.request.contextPath}/admin/rent-requests">Request</a>
    <% } else { %>
    <a href="${pageContext.request.contextPath}/rent-request">Request</a>
    <% } %>

    <a href="${pageContext.request.contextPath}/my-profile">Profile</a>
    <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
    <a href="#" data-bs-toggle="modal" data-bs-target="#logoutModal">
        Logout (<%= fullName != null ? fullName : username %>)
    </a>
    <% } else { %>
    <a href="${pageContext.request.contextPath}/login">Login</a>
    <% } %>
</div>



<div class="container-main">
    <% if (isLoggedIn) { %>
    <div class="user-info">
        <h4>Xin chào, <%= fullName != null ? fullName : username %>!</h4>
        <p>Bạn đã đăng nhập thành công.</p>
        <div style="margin-top: 15px;">
            <a href="${pageContext.request.contextPath}/my-profile" class="btn btn-primary" style="margin-right: 10px;">Xem Profile</a>
            <a href="#" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#logoutModal">Đăng xuất</a>
        </div>
    </div>
    <% } else { %>
    <h2>Welcome</h2>
    <p>Vui lòng <a href="${pageContext.request.contextPath}/login">đăng nhập</a> để sử dụng hệ thống.</p>
    <% } %>
</div>

<% if (isLoggedIn) { %>
<%@ include file="/view/authen/logout.jsp" %>
<% } %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
