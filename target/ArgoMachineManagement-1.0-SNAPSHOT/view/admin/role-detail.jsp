<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/3/2025
  Time: 10:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Role Detail</title>
</head>
<body>

<h2>Role Detail</h2>

<p><strong>ID:</strong> ${role.roleId}</p>
<p><strong>Role Name:</strong> ${role.roleName}</p>
<p><strong>Description:</strong> ${role.description}</p>
<p><strong>Status:</strong> ${role.status ? "Active" : "Inactive"}</p>

<a href="role?action=list">Back to list</a>

</body>
</html>
