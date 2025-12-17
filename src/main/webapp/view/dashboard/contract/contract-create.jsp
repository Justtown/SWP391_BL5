<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Contract - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .contract-form-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 800px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
        }
        .form-label {
            font-weight: 500;
            color: #495057;
            margin-bottom: 8px;
        }
        .form-control, .form-select {
            border-radius: 5px;
            padding: 8px 12px;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 5px;
        }
        .is-invalid {
            border-color: #dc3545;
        }
        .invalid-feedback {
            display: block;
            width: 100%;
            margin-top: 0.25rem;
            font-size: 0.875rem;
            color: #dc3545;
        }
        .btn-group-custom {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            margin-top: 30px;
        }
        .btn-back {
            background-color: #6c757d;
            border-color: #6c757d;
            color: white;
            padding: 10px 20px;
            font-weight: 500;
            border-radius: 5px;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }
        .btn-back:hover {
            background-color: #5a6268;
            border-color: #545b62;
            color: white;
            text-decoration: none;
        }
        .btn-add {
            padding: 10px 30px;
            background-color: #0d6efd;
            border-color: #0d6efd;
            color: white;
            font-weight: 500;
            border-radius: 5px;
        }
        .btn-add:hover {
            background-color: #0b5ed7;
            border-color: #0a58ca;
            color: white;
        }
        .form-text {
            font-size: 0.875rem;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid">
            <div class="contract-form-container">
                <h1 class="page-title">Create New Contract</h1>
            
            <c:if test="${not empty errors}">
                <div class="alert alert-danger">
                    <ul class="mb-0">
                        <c:forEach var="error" items="${errors}">
                            <li>${error}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger">${error}</div>
            </c:if>
            
            <form action="${pageContext.request.contextPath}/contracts" method="POST" id="createContractForm" autocomplete="off">
                <input type="hidden" name="action" value="create">
                
                <div class="mb-3">
                    <label for="contractCode" class="form-label">Contract Code <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="contractCode" name="contractCode" 
                           value="${contractCode != null ? contractCode : ''}" required readonly>
                    <small class="form-text text-muted">Auto-generated contract code</small>
                </div>
                
                <div class="mb-3">
                    <label for="customerId" class="form-label">Customer <span class="text-danger">*</span></label>
                    <select class="form-select" id="customerId" name="customerId" required>
                        <option value="">Select Customer</option>
                        <c:forEach var="customer" items="${customers}">
                            <option value="${customer.id}" ${selectedCustomerId != null && selectedCustomerId == customer.id ? 'selected' : ''}>
                                ${customer.fullName != null ? customer.fullName : customer.username} (${customer.email})
                            </option>
                        </c:forEach>
                    </select>
                    <div class="invalid-feedback" id="customerError" style="display: none;">
                        Customer and Manager cannot be the same person!
                    </div>
                    <c:if test="${empty customers}">
                        <small class="form-text text-danger">No active customers available. Please add customers first.</small>
                    </c:if>
                </div>
                
                <div class="mb-3">
                    <label for="managerId" class="form-label">Manager <span class="text-danger">*</span></label>
                    <select class="form-select" id="managerId" name="managerId" required>
                        <option value="">Select Manager</option>
                        <c:forEach var="manager" items="${managers}">
                            <option value="${manager.id}" ${selectedManagerId != null && selectedManagerId == manager.id ? 'selected' : ''}>
                                ${manager.fullName != null ? manager.fullName : manager.username} (${manager.email}) - ${manager.roleName}
                            </option>
                        </c:forEach>
                    </select>
                    <div class="invalid-feedback" id="managerError" style="display: none;">
                        Customer and Manager cannot be the same person!
                    </div>
                    <c:if test="${empty managers}">
                        <small class="form-text text-danger">No active managers/sales available. Please add managers/sales first.</small>
                    </c:if>
                </div>
                
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="startDate" class="form-label">Start Date <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="startDate" name="startDate" 
                               value="${startDate != null ? startDate : ''}" required autocomplete="off">
                    </div>
                    
                    <div class="col-md-6 mb-3">
                        <label for="endDate" class="form-label">End Date <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="endDate" name="endDate" 
                               value="${endDate != null ? endDate : ''}" required autocomplete="off">
                    </div>
                </div>
                
                <div class="mb-3">
                    <label for="note" class="form-label">Note</label>
                    <textarea class="form-control" id="note" name="note" rows="3" 
                              placeholder="Enter any additional notes..." autocomplete="off">${note != null ? note : ''}</textarea>
                </div>
                
                <div class="btn-group-custom">
                    <a href="${pageContext.request.contextPath}/contracts" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <button type="submit" class="btn btn-add">
                        <i class="fas fa-save"></i> Create Contract
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const customerSelect = document.getElementById('customerId');
        const managerSelect = document.getElementById('managerId');
        const customerError = document.getElementById('customerError');
        const managerError = document.getElementById('managerError');
        
        // Function to validate customer and manager are different
        function validateCustomerManager() {
            const customerId = customerSelect.value;
            const managerId = managerSelect.value;
            const isSame = customerId && managerId && customerId === managerId;
            
            if (isSame) {
                // Show error on both fields
                customerSelect.classList.add('is-invalid');
                managerSelect.classList.add('is-invalid');
                customerError.style.display = 'block';
                managerError.style.display = 'block';
                return false;
            } else {
                // Remove error styling
                customerSelect.classList.remove('is-invalid');
                managerSelect.classList.remove('is-invalid');
                customerError.style.display = 'none';
                managerError.style.display = 'none';
                return true;
            }
        }
        
        // Validate when customer changes
        customerSelect.addEventListener('change', function() {
            validateCustomerManager();
        });
        
        // Validate when manager changes
        managerSelect.addEventListener('change', function() {
            validateCustomerManager();
        });
        
        // Validate date range
        document.getElementById('startDate').addEventListener('change', validateDates);
        document.getElementById('endDate').addEventListener('change', validateDates);
        
        function validateDates() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            
            if (startDate && endDate && startDate > endDate) {
                alert('Start date must be before or equal to end date!');
                document.getElementById('endDate').value = '';
            }
        }
        
        // Form validation on submit
        document.getElementById('createContractForm').addEventListener('submit', function(e) {
            const customerId = customerSelect.value;
            const managerId = managerSelect.value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            
            // Check required fields
            if (!customerId || !managerId || !startDate || !endDate) {
                e.preventDefault();
                alert('Please fill in all required fields!');
                return false;
            }
            
            // Check customer and manager are different
            if (!validateCustomerManager()) {
                e.preventDefault();
                alert('Customer and Manager cannot be the same person! Please select different people.');
                return false;
            }
            
            // Check date range
            if (startDate > endDate) {
                e.preventDefault();
                alert('Start date must be before or equal to end date!');
                return false;
            }
        });
        
        // Initial validation on page load (in case of form re-display with errors)
        document.addEventListener('DOMContentLoaded', function() {
            validateCustomerManager();
        });
    </script>

    </div> <!-- end main-content -->
</body>
</html>

