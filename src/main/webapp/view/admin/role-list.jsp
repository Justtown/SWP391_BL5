<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<head>
    <title>Role List</title>
</head>
<body>

<h2>Role Management</h2>

<form method="get" action="${pageContext.request.contextPath}/admin/role">
    <input type="hidden" name="action" value="list" />
    <input type="text" name="key" placeholder="Search role"
           value="${fn:escapeXml(key)}" />
    <button type="submit">Search</button>
</form>

<br>

<table border="1" cellpadding="8">
    <tr>
        <th>ID</th>
        <th>Role</th>
        <th>Status</th>
        <th>Action</th>
    </tr>

    <c:forEach var="r" items="${roleList}">
        <tr>
            <td>${r.roleId}</td>
            <td>${r.roleName}</td>
            <td>
                <c:choose>
                    <c:when test="${r.status}">Active</c:when>
                    <c:otherwise>Deactive</c:otherwise>
                </c:choose>
            </td>
            <td>
                <a href="${pageContext.request.contextPath}/admin/role?action=detail&id=${r.roleId}">
                    View
                </a>
            </td>
        </tr>
    </c:forEach>
</table>

<br>

<div>
    <c:forEach begin="1" end="${totalPages}" var="p">
        <c:choose>
            <c:when test="${p == currentPage}">
                <strong>[${p}]</strong>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/admin/role?action=list&page=${p}&key=${key}">
                        ${p}
                </a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

</body>
</html>
