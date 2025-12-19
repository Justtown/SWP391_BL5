<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Phân quyền theo Role</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid mt-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h3><i class="fas fa-users-cog"></i> Phân quyền theo Role</h3>
        <a href="${pageContext.request.contextPath}/admin/permissions" class="btn btn-secondary">
            <i class="fas fa-arrow-left"></i> Quay lại
        </a>
    </div>

    <c:if test="${param.success != null}">
        <div class="alert alert-success alert-dismissible fade show">
            <c:choose>
                <c:when test="${param.success == 'assigned'}">Gán quyền thành công!</c:when>
                <c:when test="${param.success == 'removed'}">Hủy quyền thành công!</c:when>
            </c:choose>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card mb-3">
        <div class="card-header bg-info text-white">
            <h5>Chọn Role</h5>
        </div>
        <div class="card-body">
            <div class="d-flex gap-2 flex-wrap">
                <c:forEach var="role" items="${roles}">
                    <a href="${pageContext.request.contextPath}/admin/permissions?action=role-permissions&roleId=${role.id}" 
                       class="btn ${selectedRoleId == role.id ? 'btn-primary' : 'btn-outline-primary'}">
                        ${role.roleName}
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <c:if test="${selectedRoleId != null}">
        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5><i class="fas fa-check"></i> Quyền hiện có</h5>
                    </div>
                    <div class="card-body">
                        <c:forEach var="p" items="${rolePermissions}">
                            <div class="d-flex justify-content-between align-items-center mb-2 p-2 border">
                                <div>
                                    <strong>${p.permissionName}</strong><br>
                                    <small class="text-muted">${p.description}</small><br>
                                    <code>${p.urlPattern}</code>
                                </div>
                                <form method="POST" action="${pageContext.request.contextPath}/admin/permissions" style="display:inline">
                                    <input type="hidden" name="action" value="remove">
                                    <input type="hidden" name="roleId" value="${selectedRoleId}">
                                    <input type="hidden" name="permissionId" value="${p.id}">
                                    <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Hủy quyền?')">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </form>
                            </div>
                        </c:forEach>
                        <c:if test="${empty rolePermissions}">
                            <p class="text-muted text-center">Chưa có quyền nào</p>
                        </c:if>
                    </div>
                </div>
            </div>

            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-warning">
                        <h5><i class="fas fa-plus"></i> Quyền khả dụng</h5>
                    </div>
                    <div class="card-body">
                        <c:forEach var="p" items="${allPermissions}">
                            <c:set var="hasPermission" value="false"/>
                            <c:forEach var="rp" items="${rolePermissions}">
                                <c:if test="${rp.id == p.id}">
                                    <c:set var="hasPermission" value="true"/>
                                </c:if>
                            </c:forEach>
                            
                            <c:if test="${!hasPermission}">
                                <div class="d-flex justify-content-between align-items-center mb-2 p-2 border">
                                    <div>
                                        <strong>${p.permissionName}</strong><br>
                                        <small class="text-muted">${p.description}</small><br>
                                        <code>${p.urlPattern}</code>
                                    </div>
                                    <form method="POST" action="${pageContext.request.contextPath}/admin/permissions" style="display:inline">
                                        <input type="hidden" name="action" value="assign">
                                        <input type="hidden" name="roleId" value="${selectedRoleId}">
                                        <input type="hidden" name="permissionId" value="${p.id}">
                                        <button type="submit" class="btn btn-sm btn-success">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </form>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
