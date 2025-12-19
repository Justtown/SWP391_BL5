<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/17/2025
  Time: 10:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chi tiết máy (Customer)</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
</head>

<body>

<jsp:include page="/view/common/dashboard/sideBar.jsp"/>

<div class="main-content">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="mb-0">
            <i class="fas fa-info-circle me-2"></i>
            Chi tiết máy #${machine.id}
        </h4>

        <a href="${ctx}/customer/machines" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Quay lại
        </a>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0">
                <i class="fas fa-tractor me-2"></i>
                Thông tin máy
            </h5>
        </div>

        <div class="card-body">

            <div class="mb-3">
                <label class="form-label fw-semibold">Mã máy</label>
                <input class="form-control" value="${machine.machineCode}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Tên máy</label>
                <input class="form-control" value="${machine.machineName}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Loại máy</label>
                <input class="form-control" value="${machine.machineTypeName}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Trạng thái</label>
                <input class="form-control" value="${machine.status}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Có thể thuê</label>
                <input class="form-control"
                       value="${machine.isRentable ? 'Có' : 'Không'}"
                       readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Vị trí</label>
                <input class="form-control" value="${machine.location}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Ngày mua</label>
                <input class="form-control" value="${machine.purchaseDate}" readonly>
            </div>

            <div class="mb-3">
                <label class="form-label fw-semibold">Mô tả</label>
                <textarea class="form-control" rows="4" readonly>${machine.description}</textarea>
            </div>

        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

