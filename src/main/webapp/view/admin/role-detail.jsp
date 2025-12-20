<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/7/2025
  Time: 8:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Role Detail</title>
</head>
<body>

<h2>Role Detail</h2>
<form action="${pageContext.request.contextPath}/admin/role" method="post">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="role_id" value="${role.roleId}">
    <input type="hidden" name="role_name" value="${role.roleName}">
    <p>
        <strong>Username:</strong><br>
        <input type="text" value="${role.username}" disabled>
    </p>
    <p>
        <strong>Role Name:</strong><br>
        <input type="text" value="${role.roleName}" disabled>
    </p>
    <p>
        <strong>Description:</strong><br>
        <textarea name="description" rows="4" cols="40">${role.description}</textarea>
    </p>
    <p>
        <strong>Status:</strong><br>
        <label>
            <input type="radio" name="status" value="1"
            ${role.status ? "checked" : ""}>
            Active
        </label>
        <label>
            <input type="radio" name="status" value="0"
            ${!role.status ? "checked" : ""}>
            Deactive
        </label>
    </p>
    <br>
    <button type="submit">Save</button>
    <a href="${pageContext.request.contextPath}/admin/role?action=list">Back to list</a>
</form>
</body>
</html>


