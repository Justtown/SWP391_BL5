<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>My Contract Detail - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />

<div class="main-content">
    <div class="container-fluid">
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0">
                    <i class="fas fa-file-contract me-2"></i>
                    My Contract: ${contract.contractCode}
                </h5>
                <span class="badge bg-primary text-uppercase">
                    ${contract.status}
                </span>
            </div>
            <div class="card-body">
                <h5 class="mb-3">
                    <i class="fas fa-info-circle me-2"></i>Thông tin hợp đồng
                </h5>
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Mã hợp đồng</h6>
                        <p class="mb-1 fw-semibold">${contract.contractCode}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Tên khách hàng</h6>
                        <p class="mb-1 fw-semibold">${contract.customerName != null ? contract.customerName : 'N/A'}</p>
                    </div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Số điện thoại</h6>
                        <p class="mb-1">${contract.customerPhone != null ? contract.customerPhone : 'N/A'}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Địa chỉ</h6>
                        <p class="mb-1">${contract.customerAddress != null ? contract.customerAddress : 'N/A'}</p>
                    </div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Manager</h6>
                        <p class="mb-1 fw-semibold">${contract.managerName != null ? contract.managerName : 'N/A'}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Máy</h6>
                        <p class="mb-1">
                            <strong>${contract.machineCode != null ? contract.machineCode : '-'}</strong>
                            <span class="text-muted">-</span>
                            ${contract.machineName != null ? contract.machineName : '-'}
                        </p>
                        <small class="text-muted">
                            Loại: ${contract.machineTypeName != null ? contract.machineTypeName : (contract.machineTypeId != null ? contract.machineTypeId : 'N/A')}
                        </small>
                    </div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Số lượng</h6>
                        <p class="mb-1">${contract.quantity != null ? contract.quantity : 'N/A'}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Tổng giá trị</h6>
                        <p class="mb-1 fw-semibold text-success">
                            <c:choose>
                                <c:when test="${not empty contract.totalCost}">
                                    <fmt:formatNumber value="${contract.totalCost}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Ngày bắt đầu (thuê)</h6>
                        <p class="mb-1">${contract.startDate}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Ngày kết thúc (thuê)</h6>
                        <p class="mb-1">${contract.endDate}</p>
                    </div>
                </div>
                
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Ngày tạo (ngày tạo hợp đồng)</h6>
                        <p class="mb-1">
                            <c:if test="${not empty contract.createdAt}">
                                <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                            </c:if>
                            <c:if test="${empty contract.createdAt}">
                                N/A
                            </c:if>
                        </p>
                    </div>
                </div>

                <div class="mb-3">
                    <h6 class="text-muted">Mô tả dịch vụ</h6>
                    <p class="mb-1">
                        <c:choose>
                            <c:when test="${not empty contract.serviceDescription}">
                                ${contract.serviceDescription}
                            </c:when>
                            <c:when test="${not empty contract.note}">
                                ${contract.note}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">-</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                <a href="${pageContext.request.contextPath}/customer/contracts" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-1"></i> Back to My Contracts
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

