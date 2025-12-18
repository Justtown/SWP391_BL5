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
    <title>Role Detail</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp"/>

<div class="main-content">
    <h4 class="mb-4">
        <i class="fas fa-info-circle me-2"></i> Role Detail
    </h4>

    <form method="post" action="${ctx}/admin/role-management">
        <input type="hidden" name="roleId" value="${role.roleId}"/>

        <div class="mb-3">
            <label class="form-label">Role Name</label>
            <input class="form-control" value="${role.roleName}" readonly>
        </div>

        <div class="mb-3">
            <label class="form-label">Description</label>
            <textarea name="description"
                      class="form-control"
                      rows="3">${role.description}</textarea>
        </div>

        <div class="mb-4">
            <label class="form-label">Status</label>
            <select name="status" class="form-select">
                <option value="1" ${role.status ? 'selected' : ''}>ACTIVE</option>
                <option value="0" ${!role.status ? 'selected' : ''}>INACTIVE</option>
            </select>
        </div>

        <div class="d-flex justify-content-end gap-2">
            <a href="${ctx}/admin/role-management" class="btn btn-secondary">Back</a>
            <button class="btn btn-success">Save</button>
        </div>
    </form>
</div>
</body>
</html>
