<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Detail - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .contract-detail-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        .detail-header {
            border-bottom: 2px solid #dee2e6;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        .status-badge {
            padding: 6px 16px;
            border-radius: 20px;
            font-size: 0.9rem;
            font-weight: 500;
        }
        .status-DRAFT {
            background-color: #ffc107;
            color: #000;
        }
        .status-ACTIVE {
            background-color: #28a745;
            color: #fff;
        }
        .status-APPROVED {
            background-color: #28a745;
            color: #fff;
        }
        .status-FINISHED {
            background-color: #6c757d;
            color: #fff;
        }
        .status-PENDING {
            background-color: #ffc107;
            color: #000;
        }
        .status-CANCELLED {
            background-color: #dc3545;
            color: #fff;
        }
        .status-REJECTED {
            background-color: #dc3545;
            color: #fff;
        }
        .info-row {
            padding: 12px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 5px;
        }
        .info-value {
            color: #212529;
        }
    </style>
</head>
<body>
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <div class="container-fluid">
            <div class="contract-detail-container">
                <div class="detail-header d-flex justify-content-between align-items-center">
                    <h2 class="mb-0">
                        <i class="fas fa-file-contract me-2"></i>Contract Detail
                    </h2>
                    <span class="status-badge status-${contract.status}">
                        ${contract.status}
                    </span>
                </div>

                <!-- Contract Information Section -->
                <h5 class="mb-3 mt-4">
                    <i class="fas fa-info-circle me-2"></i>Thông tin hợp đồng
                </h5>
                
                <div class="row">
                    <div class="col-md-6">
                        <div class="info-row">
                            <div class="info-label">Mã hợp đồng</div>
                            <div class="info-value">
                                <strong>${contract.contractCode}</strong>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Tên khách hàng</div>
                            <div class="info-value">
                                ${contract.customerName != null ? contract.customerName : 'N/A'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Số điện thoại</div>
                            <div class="info-value">
                                ${contract.customerPhone != null ? contract.customerPhone : 'N/A'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Địa chỉ</div>
                            <div class="info-value">
                                ${contract.customerAddress != null ? contract.customerAddress : 'N/A'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Manager</div>
                            <div class="info-value">
                                ${contract.managerName != null ? contract.managerName : 'N/A'}
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="info-row">
                            <div class="info-label">Mã máy</div>
                            <div class="info-value">
                                ${contract.machineCode != null ? contract.machineCode : '-'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Tên máy</div>
                            <div class="info-value">
                                ${contract.machineName != null ? contract.machineName : '-'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Loại máy</div>
                            <div class="info-value">
                                ${contract.machineTypeName != null ? contract.machineTypeName : (contract.machineTypeId != null ? contract.machineTypeId : 'N/A')}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Số lượng</div>
                            <div class="info-value">
                                ${contract.quantity != null ? contract.quantity : 'N/A'}
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Ngày bắt đầu (thuê)</div>
                            <div class="info-value">
                                <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Ngày kết thúc (thuê)</div>
                            <div class="info-value">
                                <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Tổng giá trị</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${not empty contract.totalCost}">
                                        <fmt:formatNumber value="${contract.totalCost}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                    </c:when>
                                    <c:otherwise>N/A</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Ngày tạo (ngày tạo hợp đồng)</div>
                            <div class="info-value">
                                <c:if test="${not empty contract.createdAt}">
                                    <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:if>
                                <c:if test="${empty contract.createdAt}">
                                    N/A
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty contract.note}">
                    <div class="mt-4">
                        <div class="info-label">Ghi chú</div>
                        <div class="info-value p-3 bg-light rounded">
                            ${contract.note}
                        </div>
                    </div>
                </c:if>

                <div class="mt-4 d-flex justify-content-between">
                    <c:set var="contractsPath" value="/contracts" />
                    <c:if test="${sessionScope.roleName == 'manager'}">
                        <c:set var="contractsPath" value="/manager/contracts" />
                    </c:if>
                    <c:if test="${sessionScope.roleName == 'sale'}">
                        <c:set var="contractsPath" value="/sale/contracts" />
                    </c:if>
                    <a href="${pageContext.request.contextPath}${contractsPath}" class="btn btn-secondary">
                        <i class="fas fa-arrow-left me-1"></i> Back to Contract List
                    </a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
