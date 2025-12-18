<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Quản lý phân quyền</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .permission-matrix {
            overflow-x: auto;
        }
        .permission-matrix table {
            white-space: nowrap;
        }
        .permission-matrix th {
            position: sticky;
            top: 0;
            background: #343a40;
            color: white;
            z-index: 10;
            text-align: center;
        }
        .permission-matrix td:first-child,
        .permission-matrix th:first-child {
            position: sticky;
            left: 0;
            background: white;
            z-index: 5;
            text-align: left;
        }
        .permission-matrix th:first-child {
            z-index: 15;
            background: #343a40;
        }
        .screen-name {
            min-width: 300px;
            padding: 12px;
            font-weight: 500;
        }
        .role-header {
            min-width: 120px;
            text-align: center;
            padding: 12px;
        }
        .mark-cell {
            text-align: center;
            vertical-align: middle;
            cursor: pointer;
            user-select: none;
            transition: background-color 0.2s;
        }
        .mark-cell:hover {
            background-color: #f8f9fa;
        }
        .mark-cell.has-permission {
            background-color: #d4edda;
        }
        .mark-cell .mark-x {
            font-size: 20px;
            font-weight: bold;
            color: #28a745;
        }
        .mark-cell.no-permission .mark-x {
            display: none;
        }
    </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3><i class="fas fa-shield-alt"></i> Quản lý phân quyền</h3>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Quay lại
        </a>
    </div>

    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show">
            Cập nhật quyền thành công!
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <form method="POST" action="${pageContext.request.contextPath}/admin/permissions">
        <input type="hidden" name="action" value="save-matrix">
        
        <div class="card">
            <div class="card-body permission-matrix">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th class="screen-name">Screen (URL Pattern)</th>
                            <c:forEach var="role" items="${roles}">
                                <th class="role-header">
                                    <div><strong>${role.roleName}</strong></div>
                                    <small class="text-muted">${role.description}</small>
                                </th>
                            </c:forEach>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="perm" items="${allPermissions}">
                            <tr>
                                <td class="screen-name">
                                    <div><strong>${perm.urlPattern}</strong></div>
                                </td>
                                <c:forEach var="role" items="${roles}">
                                    <c:set var="hasPermission" value="false"/>
                                    <c:forEach var="rp" items="${rolePermissionsMap[role.roleId]}">
                                        <c:if test="${rp.id == perm.id}">
                                            <c:set var="hasPermission" value="true"/>
                                        </c:if>
                                    </c:forEach>
                                    
                                    <td class="mark-cell ${hasPermission ? 'has-permission' : 'no-permission'}" 
                                        onclick="togglePermission(this)">
                                        <input type="checkbox" 
                                               name="permission_${role.roleId}_${perm.id}"
                                               value="1"
                                               style="display: none;"
                                               ${hasPermission ? 'checked' : ''}>
                                        <span class="mark-x">X</span>
                                    </td>
                                </c:forEach>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
        
        <div class="mt-3 text-end">
            <button type="submit" class="btn btn-success btn-lg">
                <i class="fas fa-save me-2"></i>Lưu thay đổi
            </button>
        </div>
    </form>
</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function togglePermission(cell) {
    const checkbox = cell.querySelector('input[type="checkbox"]');
    checkbox.checked = !checkbox.checked;
    
    if (checkbox.checked) {
        cell.classList.remove('no-permission');
        cell.classList.add('has-permission');
    } else {
        cell.classList.remove('has-permission');
        cell.classList.add('no-permission');
    }
}
</script>
</body>
</html>
