<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Customer</h6>
                        <p class="mb-1 fw-semibold">${contract.customerName}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">Manager</h6>
                        <p class="mb-1 fw-semibold">${contract.managerName}</p>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <h6 class="text-muted">Start Date</h6>
                        <p class="mb-1">${contract.startDate}</p>
                    </div>
                    <div class="col-md-6">
                        <h6 class="text-muted">End Date</h6>
                        <p class="mb-1">${contract.endDate}</p>
                    </div>
                </div>

                <div class="mb-3">
                    <h6 class="text-muted">Note</h6>
                    <p class="mb-1">
                        <c:choose>
                            <c:when test="${not empty contract.note}">
                                ${contract.note}
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">No note</span>
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

