<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Role Detail</title>
</head>
<body>

<h2>Role Detail</h2>

<form action="${pageContext.request.contextPath}/admin/role" method="post">
    <input type="hidden" name="action" value="update" />
    <input type="hidden" name="role_id" value="${role.roleId}" />

    <p>
        <strong>Role Name:</strong><br/>
        <input type="text" value="${role.roleName}" disabled/>
    </p>

    <p>
        <strong>Description:</strong><br/>
        <textarea name="description" rows="4" cols="40">${role.description}</textarea>
    </p>

    <p>
        <strong>Status:</strong><br/>
        <label>
            <input type="radio" name="status" value="1"
                   <c:if test="${role.status}">checked</c:if> /> Active
        </label>
        <label>
            <input type="radio" name="status" value="0"
                   <c:if test="${!role.status}">checked</c:if> /> Deactive
        </label>
    </p>

    <button type="submit">Save</button>
    <a href="${pageContext.request.contextPath}/admin/role?action=list">Back to list</a>
</form>

</body>
</html>
