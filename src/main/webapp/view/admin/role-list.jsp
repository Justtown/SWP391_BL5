<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/3/2025
  Time: 10:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Role List</title>
</head>
<body>

<h2>Role List</h2>

<table border="1" cellpadding="10">
    <tr>
        <th>ID</th>
        <th>Role Name</th>
        <th>Action</th>
    </tr>

    <c:forEach var="r" items="${roles}">
        <tr>
            <td>${r.roleId}</td>
            <td>${r.roleName}</td>
            <td>
                <a href="role?action=detail&id=${r.roleId}">View Detail</a>
            </td>
        </tr>
    </c:forEach>

</table>

</body>
</html>

