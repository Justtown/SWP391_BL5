<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${permission != null ? 'Sửa' : 'Thêm'} Permission</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid mt-4">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h4><i class="fas fa-shield-alt"></i> ${permission != null ? 'Sửa Permission' : 'Thêm Permission'}</h4>
                </div>
                <div class="card-body">
                    <form method="POST" action="${pageContext.request.contextPath}/admin/permissions">
                        <input type="hidden" name="action" value="${permission != null ? 'update' : 'create'}">
                        <c:if test="${permission != null}">
                            <input type="hidden" name="id" value="${permission.id}">
                        </c:if>

                        <div class="mb-3">
                            <label class="form-label">Permission Name *</label>
                            <input type="text" class="form-control" name="permissionName" 
                                   value="${permission != null ? permission.permissionName : ''}" required 
                                   placeholder="Ex: user.view, order.create">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Description</label>
                            <textarea class="form-control" name="description" rows="3" 
                                      placeholder="Mô tả quyền">${permission != null ? permission.description : ''}</textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">URL Pattern *</label>
                            <input type="text" class="form-control" name="urlPattern" 
                                   value="${permission != null ? permission.urlPattern : ''}" required 
                                   placeholder="Ex: /admin/users, /manager/orders">
                        </div>

                        <div class="d-flex justify-content-between">
                            <a href="${pageContext.request.contextPath}/admin/permissions" class="btn btn-secondary">
                                <i class="fas fa-times"></i> Hủy
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Lưu
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
