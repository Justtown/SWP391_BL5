<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết đơn hàng - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .status-badge-lg {
            font-size: 1.2rem;
            padding: 0.5rem 1rem;
        }
        .info-row {
            border-bottom: 1px solid #dee2e6;
            padding: 0.75rem 0;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
        }
    </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid">
<div class="container mt-4">
    <div class="card shadow-sm">
        <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
            <h4 class="mb-0"><i class="bi bi-file-text"></i> Chi tiết đơn hàng #${order.id}</h4>
            <c:choose>
                <c:when test="${order.status == 'PENDING'}">
                    <span class="badge bg-warning text-dark status-badge-lg">Chờ duyệt</span>
                </c:when>
                <c:when test="${order.status == 'APPROVED'}">
                    <span class="badge bg-success status-badge-lg">Đã duyệt</span>
                </c:when>
                <c:when test="${order.status == 'REJECTED'}">
                    <span class="badge bg-danger status-badge-lg">Từ chối</span>
                </c:when>
                <c:when test="${order.status == 'IN_PROGRESS'}">
                    <span class="badge bg-primary status-badge-lg">Đang xử lý</span>
                </c:when>
                <c:when test="${order.status == 'COMPLETED'}">
                    <span class="badge bg-secondary status-badge-lg">Hoàn thành</span>
                </c:when>
                <c:when test="${order.status == 'CANCELLED'}">
                    <span class="badge bg-dark status-badge-lg">Đã hủy</span>
                </c:when>
            </c:choose>
        </div>
        <div class="card-body">
            <!-- Order Information -->
            <h5 class="text-primary mb-3"><i class="bi bi-info-circle"></i> Thông tin hợp đồng</h5>
            
            <div class="row">
                <div class="col-md-6">
                    <div class="info-row">
                        <div class="info-label">Mã hợp đồng:</div>
                        <div class="fw-bold text-dark">${order.contractCode}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Tên khách hàng:</div>
                        <div>${order.customerName}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Số điện thoại:</div>
                        <div>${order.customerPhone != null ? order.customerPhone : '-'}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Địa chỉ:</div>
                        <div>${order.customerAddress != null ? order.customerAddress : '-'}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Mã máy:</div>
                        <div class="fw-bold text-primary">${order.machineCode != null ? order.machineCode : '-'}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Tên máy:</div>
                        <div class="fw-bold">${order.machineName != null ? order.machineName : '-'}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Loại máy:</div>
                        <div class="text-success">${order.machineTypeName != null ? order.machineTypeName : '-'}</div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Số lượng:</div>
                        <div><span class="badge bg-secondary fs-6">${order.quantity != null ? order.quantity : '-'}</span></div>
                    </div>
                </div>
                
                <div class="col-md-6">
                    <div class="info-row">
                        <div class="info-label">Ngày bắt đầu:</div>
                        <div><fmt:formatDate value="${order.startDate}" pattern="dd/MM/yyyy"/></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Ngày kết thúc dự kiến:</div>
                        <div>
                            <c:choose>
                                <c:when test="${order.endDate != null}">
                                    <fmt:formatDate value="${order.endDate}" pattern="dd/MM/yyyy"/>
                                </c:when>
                                <c:otherwise>-</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Tổng giá trị:</div>
                        <div class="text-success fw-bold">
                            <c:choose>
                                <c:when test="${order.totalCost != null}">
                                    <fmt:formatNumber value="${order.totalCost}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                </c:when>
                                <c:otherwise>Chưa xác định</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Ngày tạo:</div>
                        <div><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                    </div>
                    <div class="info-row">
                        <div class="info-label">Cập nhật lần cuối:</div>
                        <div><fmt:formatDate value="${order.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></div>
                    </div>
                </div>
            </div>

            <!-- Service Description -->
            <div class="mt-4">
                <h5 class="text-primary mb-3"><i class="bi bi-card-text"></i> Mô tả dịch vụ</h5>
                <div class="p-3 bg-light border rounded">
                    <c:choose>
                        <c:when test="${order.serviceDescription != null}">
                            ${order.serviceDescription}
                        </c:when>
                        <c:otherwise>
                            <em class="text-muted">Chưa có mô tả</em>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- User Information -->
            <div class="mt-4">
                <h5 class="text-primary mb-3"><i class="bi bi-people"></i> Thông tin người dùng</h5>
                <div class="row">
                    <div class="col-md-6">
                        <div class="card border-primary">
                            <div class="card-body">
                                <h6 class="card-title"><i class="bi bi-person-plus"></i> Người tạo đơn</h6>
                                <p class="card-text fw-bold">${order.createdByName}</p>
                                <small class="text-muted">Ngày tạo: <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card ${order.approvedBy != null ? 'border-success' : 'border-secondary'}">
                            <div class="card-body">
                                <h6 class="card-title"><i class="bi bi-person-check"></i> Người phê duyệt</h6>
                                <p class="card-text fw-bold">
                                    <c:choose>
                                        <c:when test="${order.approvedBy != null}">
                                            ${order.approvedByName}
                                        </c:when>
                                        <c:otherwise>
                                            <em class="text-muted">Chưa được duyệt</em>
                                        </c:otherwise>
                                    </c:choose>
                                </p>
                                <c:if test="${order.approvedBy != null}">
                                    <small class="text-muted">Ngày duyệt: <fmt:formatDate value="${order.updatedAt}" pattern="dd/MM/yyyy HH:mm"/></small>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Action Buttons -->
            <div class="mt-4 d-flex justify-content-between">
                <a href="${pageContext.request.contextPath}/manager/orders?action=list" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay lại danh sách
                </a>
                
                <div class="btn-group">
                    <!-- Manager can approve/reject PENDING orders -->
                    <c:if test="${order.status == 'PENDING'}">
                        <button type="button" class="btn btn-success" 
                                onclick="if(confirm('Bạn có chắc muốn duyệt đơn hàng này?')) { 
                                    location.href='${pageContext.request.contextPath}/manager/orders?action=approve&id=${order.id}'; }">
                            <i class="bi bi-check-circle"></i> Duyệt đơn
                        </button>
                        <button type="button" class="btn btn-danger" 
                                onclick="if(confirm('Bạn có chắc muốn từ chối đơn hàng này?')) { 
                                    location.href='${pageContext.request.contextPath}/manager/orders?action=reject&id=${order.id}'; }">
                            <i class="bi bi-x-circle"></i> Từ chối
                        </button>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>
</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
