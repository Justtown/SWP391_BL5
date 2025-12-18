<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/18/2025
  Time: 8:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html>
<head>
    <title>Role Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp"/>

<div class="main-content">
    <h4 class="mb-4">
        <i class="fas fa-user-shield me-2"></i> Role List
    </h4>
    <a href="${ctx}/admin/dashboard" class="btn btn-secondary mb-3">
        ← Back
    </a>
    <table class="table table-bordered">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Role Name</th>
            <th>Description</th>
            <th>Status</th>
            <th class="text-center">Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="r" items="${roles}">
            <tr>
                <td>${r.roleId}</td>
                <td>${r.roleName}</td>
                <td>${r.description}</td>
                <td>
                    <span class="badge ${r.status ? 'bg-success' : 'bg-danger'}">
                            ${r.status ? 'ACTIVE' : 'INACTIVE'}
                    </span>
                </td>
                <td class="text-center">
                    <a class="btn btn-sm btn-outline-primary"
                       href="${ctx}/admin/role-management?action=detail&id=${r.roleId}">
                        Detail
                    </a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>
</body>
</html>
