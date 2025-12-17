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
        .status-FINISHED {
            background-color: #6c757d;
            color: #fff;
        }
        .status-CANCELLED {
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

                <div class="row">
                    <div class="col-md-6">
                        <div class="info-row">
                            <div class="info-label">Contract Code</div>
                            <div class="info-value">
                                <strong>${contract.contractCode}</strong>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Customer</div>
                            <div class="info-value">
                                ${contract.customerName != null ? contract.customerName : 'N/A'}
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
                            <div class="info-label">Start Date</div>
                            <div class="info-value">
                                <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">End Date</div>
                            <div class="info-value">
                                <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>
                        <div class="info-row">
                            <div class="info-label">Created At</div>
                            <div class="info-value">
                                <c:if test="${not empty contract.createdAt}">
                                    <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty contract.note}">
                    <div class="mt-4">
                        <div class="info-label">Note</div>
                        <div class="info-value p-3 bg-light rounded">
                            ${contract.note}
                        </div>
                    </div>
                </c:if>

                <!-- Machines in Contract Section -->
                <div class="mt-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="mb-0">
                            <i class="fas fa-cogs me-2"></i>Machines in Contract
                        </h5>
                        <c:if test="${sessionScope.roleName == 'manager' || sessionScope.roleName == 'admin'}">
                            <c:set var="contractsPath" value="/contracts" />
                            <c:if test="${sessionScope.roleName == 'manager'}">
                                <c:set var="contractsPath" value="/manager/contracts" />
                            </c:if>
                            <a href="${pageContext.request.contextPath}${contractsPath}?action=add-machine&contractId=${contract.id}" 
                               class="btn btn-primary btn-sm">
                                <i class="fas fa-plus me-1"></i> Add Machine
                            </a>
                        </c:if>
                    </div>
                    
                    <c:choose>
                        <c:when test="${not empty contractItems && contractItems.size() > 0}">
                            <div class="table-responsive">
                                <table class="table table-hover">
                                    <thead>
                                        <tr>
                                            <th>Machine Code</th>
                                            <th>Machine Name</th>
                                            <th>Status</th>
                                            <th>Note</th>
                                            <c:if test="${sessionScope.roleName == 'manager' || sessionScope.roleName == 'admin'}">
                                                <th>Action</th>
                                            </c:if>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="item" items="${contractItems}">
                                            <tr>
                                                <td><strong>${item.machineCode != null ? item.machineCode : 'N/A'}</strong></td>
                                                <td>${item.machineName != null ? item.machineName : item.machineNameSnapshot}</td>
                                                <td>
                                                    <span class="badge bg-info">${item.machineStatus != null ? item.machineStatus : 'N/A'}</span>
                                                </td>
                                                <td>${item.note != null ? item.note : '-'}</td>
                                                <c:if test="${sessionScope.roleName == 'manager' || sessionScope.roleName == 'admin'}">
                                                    <td>
                                                        <form action="${pageContext.request.contextPath}${contractsPath}" 
                                                              method="POST" style="display: inline;" 
                                                              onsubmit="return confirm('Are you sure you want to remove this machine from the contract?');">
                                                            <input type="hidden" name="action" value="remove-machine">
                                                            <input type="hidden" name="itemId" value="${item.id}">
                                                            <input type="hidden" name="contractId" value="${contract.id}">
                                                            <button type="submit" class="btn btn-danger btn-sm">
                                                                <i class="fas fa-trash"></i> Remove
                                                            </button>
                                                        </form>
                                                    </td>
                                                </c:if>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle me-2"></i>No machines added to this contract yet.
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
                        <i class="fas fa-check-circle me-2"></i>${param.success}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger alert-dismissible fade show mt-3" role="alert">
                        <i class="fas fa-exclamation-circle me-2"></i>${param.error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
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
