<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết hợp đồng - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: #f8f9fa;
        }
        
        .page-header {
            background: white;
            border-bottom: 1px solid #dee2e6;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .breadcrumb {
            margin-bottom: 0;
        }
        
        .breadcrumb-item a {
            color: #0d6efd;
            text-decoration: none;
        }
        
        .breadcrumb-item a:hover {
            text-decoration: underline;
        }
        
        .content-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
    </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />

<div class="main-content">
    <!-- Page Header -->
    <div class="page-header d-flex justify-content-between align-items-center">
        <div>
            <h4 class="mb-1">
                <i class="fas fa-file-contract me-2"></i>
                Chi tiết hợp đồng<c:if test="${contract != null}">: ${contract.contractCode}</c:if>
            </h4>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a>
                    </li>
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/customer/my-contracts">Hợp đồng</a>
                    </li>
                    <li class="breadcrumb-item active">Chi tiết</li>
                </ol>
            </nav>
        </div>
        <div class="d-flex align-items-center">
            <span class="me-3">
                <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
            </span>
        </div>
    </div>
    <div class="container-fluid">
        <c:choose>
            <c:when test="${contract != null}">
                <div class="content-card">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="mb-0">
                            <i class="fas fa-file-contract me-2"></i>
                            Hợp đồng: ${contract.contractCode}
                        </h5>
                        <span class="badge bg-primary text-uppercase">
                            ${contract.status}
                        </span>
                    </div>
                    
                    <div>
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
                        <h6 class="text-muted">Tổng giá trị</h6>
                        <p class="mb-1 fw-semibold text-success">
                            <c:choose>
                                <c:when test="${not empty contract.items && contract.items.size() > 0}">
                                    <c:set var="totalPrice" value="0" />
                                    <c:forEach var="item" items="${contract.items}">
                                        <c:set var="totalPrice" value="${totalPrice + (item.price != null ? item.price : 0)}" />
                                    </c:forEach>
                                    <fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                </c:when>
                                <c:otherwise>N/A</c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
                
                <!-- Danh sách máy trong hợp đồng -->
                <div class="mb-4">
                    <h5 class="mb-3">
                        <i class="fas fa-cogs me-2"></i>Danh sách máy trong hợp đồng
                    </h5>
                    <c:choose>
                        <c:when test="${not empty contract.items && contract.items.size() > 0}">
                            <div class="table-responsive">
                                <table class="table table-bordered table-hover">
                                    <thead class="table-light">
                                        <tr>
                                            <th>STT</th>
                                            <th>Serial Number</th>
                                            <th>Model</th>
                                            <th>Brand</th>
                                            <th>Loại máy</th>
                                            <th>Giá thuê</th>
                                            <th>Ghi chú</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${contract.items}" varStatus="loop">
                                            <tr>
                                                <td>${loop.index + 1}</td>
                                                <td><strong>${item.serialNumber != null ? item.serialNumber : 'N/A'}</strong></td>
                                                <td>${item.modelName != null ? item.modelName : 'N/A'}</td>
                                                <td>${item.brand != null ? item.brand : 'N/A'}</td>
                                                <td>${item.typeName != null ? item.typeName : 'N/A'}</td>
                                                <td class="text-success fw-semibold">
                                                    <c:choose>
                                                        <c:when test="${not empty item.price}">
                                                            <fmt:formatNumber value="${item.price}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                                        </c:when>
                                                        <c:otherwise>N/A</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${item.note != null ? item.note : '-'}</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>Chưa có máy nào trong hợp đồng này.
                            </div>
                        </c:otherwise>
                    </c:choose>
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
                    <h6 class="text-muted">Ghi chú</h6>
                    <p class="mb-1">
                        <c:choose>
                            <c:when test="${not empty contract.note}">
                                ${contract.note}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">-</span>
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>

                        <div class="mt-4">
                            <a href="${pageContext.request.contextPath}/customer/my-contracts" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách hợp đồng
                            </a>
                        </div>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="content-card">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Không tìm thấy thông tin hợp đồng. Vui lòng thử lại.
                    </div>
                    <a href="${pageContext.request.contextPath}/customer/my-contracts" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách hợp đồng
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

