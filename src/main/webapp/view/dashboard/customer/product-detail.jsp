<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết sản phẩm - Argo Machine Management</title>
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
        
        .status-badge {
            padding: 6px 16px;
            border-radius: 12px;
            font-size: 0.875rem;
            font-weight: 500;
        }
        
        .status-ACTIVE {
            background-color: #28a745;
            color: #fff;
        }
        
        .status-FINISHED {
            background-color: #6c757d;
            color: #fff;
        }
        
        .info-section {
            margin-bottom: 2rem;
        }
        
        .info-section h5 {
            color: #495057;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #e9ecef;
        }
        
        .info-row {
            margin-bottom: 1rem;
        }
        
        .info-label {
            color: #6c757d;
            font-size: 0.875rem;
            margin-bottom: 0.25rem;
        }
        
        .info-value {
            font-size: 1rem;
            font-weight: 500;
            color: #212529;
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
                <i class="fas fa-box me-2"></i>
                Chi tiết sản phẩm<c:if test="${product != null}">: ${product.serialNumber != null ? product.serialNumber : 'N/A'}</c:if>
            </h4>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a>
                    </li>
                    <li class="breadcrumb-item">
                        <a href="${pageContext.request.contextPath}/customer/products">Sản phẩm</a>
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
            <c:when test="${product != null}">
                <div class="content-card">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="mb-0">
                            <i class="fas fa-cogs me-2"></i>
                            Thông tin máy: ${product.serialNumber != null ? product.serialNumber : 'N/A'}
                        </h5>
                        <span class="status-badge status-${product.contractStatus}">
                            <c:choose>
                                <c:when test="${product.contractStatus == 'ACTIVE'}">Đang thuê</c:when>
                                <c:when test="${product.contractStatus == 'FINISHED'}">Đã thuê</c:when>
                                <c:otherwise>${product.contractStatus}</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                    
                    <!-- Thông tin máy -->
                    <div class="info-section">
                        <h5>
                            <i class="fas fa-info-circle me-2"></i>Thông tin máy
                        </h5>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Serial Number</div>
                                <div class="info-value">${product.serialNumber != null ? product.serialNumber : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Model Code</div>
                                <div class="info-value">${product.modelCode != null ? product.modelCode : 'N/A'}</div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Model Name</div>
                                <div class="info-value">${product.modelName != null ? product.modelName : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Brand</div>
                                <div class="info-value">${product.brand != null ? product.brand : 'N/A'}</div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Loại máy</div>
                                <div class="info-value">${product.typeName != null ? product.typeName : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Trạng thái máy</div>
                                <div class="info-value">${product.assetStatus != null ? product.assetStatus : 'N/A'}</div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Trạng thái thuê</div>
                                <div class="info-value">${product.rentalStatus != null ? product.rentalStatus : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Giá thuê</div>
                                <div class="info-value text-success fw-semibold">
                                    <c:choose>
                                        <c:when test="${not empty product.price}">
                                            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                        </c:when>
                                        <c:otherwise>N/A</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Thông tin hợp đồng -->
                    <div class="info-section">
                        <h5>
                            <i class="fas fa-file-contract me-2"></i>Thông tin hợp đồng
                        </h5>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Mã hợp đồng</div>
                                <div class="info-value">${product.contractCode != null ? product.contractCode : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Trạng thái hợp đồng</div>
                                <div class="info-value">
                                    <span class="status-badge status-${product.contractStatus}">
                                        <c:choose>
                                            <c:when test="${product.contractStatus == 'ACTIVE'}">Đang thuê</c:when>
                                            <c:when test="${product.contractStatus == 'FINISHED'}">Đã thuê</c:when>
                                            <c:otherwise>${product.contractStatus}</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>
                        </div>
                        
                        <div class="row">
                            <div class="col-md-6 info-row">
                                <div class="info-label">Ngày bắt đầu thuê</div>
                                <div class="info-value">${product.contractStartDate != null ? product.contractStartDate : 'N/A'}</div>
                            </div>
                            <div class="col-md-6 info-row">
                                <div class="info-label">Ngày kết thúc thuê</div>
                                <div class="info-value">${product.contractEndDate != null ? product.contractEndDate : 'N/A'}</div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Ghi chú -->
                    <div class="info-section">
                        <h5>
                            <i class="fas fa-sticky-note me-2"></i>Ghi chú
                        </h5>
                        <div class="info-row">
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${not empty product.note}">
                                        ${product.note}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted">-</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>

                    <div class="mt-4">
                        <a href="${pageContext.request.contextPath}/customer/products" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách sản phẩm
                        </a>
                        <c:if test="${product.contractCode != null}">
                            <a href="${pageContext.request.contextPath}/customer/my-contracts?action=detail&id=${product.contractId}" class="btn btn-info ms-2">
                                <i class="fas fa-file-contract me-1"></i> Xem hợp đồng
                            </a>
                        </c:if>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="content-card">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-triangle me-2"></i>
                        Không tìm thấy thông tin sản phẩm. Vui lòng thử lại.
                    </div>
                    <a href="${pageContext.request.contextPath}/customer/products" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách sản phẩm
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
