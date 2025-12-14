<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Detail - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .contract-detail-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 900px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
        }
        .info-section {
            margin-bottom: 30px;
        }
        .info-section-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e9ecef;
        }
        .info-row {
            display: flex;
            margin-bottom: 15px;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .info-label {
            font-weight: 600;
            color: #6c757d;
            width: 180px;
            flex-shrink: 0;
        }
        .info-value {
            color: #212529;
            flex: 1;
        }
        .status-badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 5px;
            font-weight: 500;
            font-size: 0.875rem;
        }
        .status-DRAFT {
            background-color: #ffc107;
            color: #000;
        }
        .status-ACTIVE {
            background-color: #28a745;
            color: white;
        }
        .status-FINISHED {
            background-color: #6c757d;
            color: white;
        }
        .status-CANCELLED {
            background-color: #dc3545;
            color: white;
        }
        .btn-group-custom {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            margin-top: 30px;
        }
        .btn-back {
            background-color: #6c757d;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.3s;
        }
        .btn-back:hover {
            background-color: #5a6268;
            color: white;
        }
        .btn-add-machine {
            background-color: #0d6efd;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            transition: background-color 0.3s;
            border: none;
            cursor: pointer;
        }
        .btn-add-machine:hover {
            background-color: #0b5ed7;
            color: white;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="contract-detail-container">
            <h1 class="page-title">Contract Detail</h1>
            
            <c:if test="${empty contract}">
                <div class="alert alert-danger">Contract not found!</div>
                <a href="${pageContext.request.contextPath}/contracts" class="btn-back">
                    <i class="fas fa-arrow-left"></i> Back to Contracts
                </a>
            </c:if>
            
            <c:if test="${not empty contract}">
                <!-- Contract Information -->
                <div class="info-section">
                    <h3 class="info-section-title">Contract Information</h3>
                    
                    <div class="info-row">
                        <div class="info-label">Contract Code:</div>
                        <div class="info-value"><strong>${contract.contractCode}</strong></div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Customer:</div>
                        <div class="info-value">${contract.customerName != null ? contract.customerName : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Manager:</div>
                        <div class="info-value">${contract.managerName != null ? contract.managerName : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Start Date:</div>
                        <div class="info-value">${contract.startDate}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">End Date:</div>
                        <div class="info-value">${contract.endDate}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Status:</div>
                        <div class="info-value">
                            <span class="status-badge status-${contract.status}">
                                ${contract.status}
                            </span>
                        </div>
                    </div>
                    
                    <c:if test="${not empty contract.note}">
                        <div class="info-row">
                            <div class="info-label">Note:</div>
                            <div class="info-value">${contract.note}</div>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty contract.createdAt}">
                        <div class="info-row">
                            <div class="info-label">Created At:</div>
                            <div class="info-value">${contract.createdAt}</div>
                        </div>
                    </c:if>
                </div>
                
                <!-- Action Buttons -->
                <div class="btn-group-custom">
                    <a href="${pageContext.request.contextPath}/contracts" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Back to Contracts
                    </a>
                    <button type="button" class="btn-add-machine" onclick="alert('Coming soon')">
                        <i class="fas fa-plus"></i> Add Machine
                    </button>
                </div>
            </c:if>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

