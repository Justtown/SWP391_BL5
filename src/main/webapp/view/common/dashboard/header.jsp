<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%
    Integer odUserId = (Integer) session.getAttribute("userId");
    String odUsername = (String) session.getAttribute("username");
    String odFullName = (String) session.getAttribute("fullName");
    boolean odIsLoggedIn = (odUserId != null);
%>

<!-- Header -->
<div class="logo">
    <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="height:100px;" onerror="this.style.display='none'">
</div>

<div class="title">Argo Machine Management</div>

<div class="menu">
    <a href="${pageContext.request.contextPath}/home">Home</a>
    <a href="${pageContext.request.contextPath}/machines">Machines</a>
    <a href="${pageContext.request.contextPath}/contracts">Contracts</a>

    <% if (odIsLoggedIn) { %>
    <a href="${pageContext.request.contextPath}/my-profile">Profile</a>
    <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
    <a href="#" data-bs-toggle="modal" data-bs-target="#logoutModal">Logout (<%= odFullName != null ? odFullName : odUsername %>)</a>
    <% } else { %>
    <a href="${pageContext.request.contextPath}/login">Login</a>
    <% } %>
</div>
