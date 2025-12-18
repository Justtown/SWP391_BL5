<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="contractsPath" value="/contracts" />
<c:if test="${sessionScope.roleName == 'manager'}"><c:set var="contractsPath" value="/manager/contracts" /></c:if>
<c:if test="${sessionScope.roleName == 'sale'}"><c:set var="contractsPath" value="/sale/contracts" /></c:if>
<c:if test="${sessionScope.roleName == 'customer'}"><c:set var="contractsPath" value="/customer/contracts" /></c:if>
<c:set var="contractsBase" value="${pageContext.request.contextPath}${contractsPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Contract - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .contract-form-container {
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
        .info-badge {
            background-color: #e7f3ff;
            color: #0066cc;
            padding: 8px 12px;
            border-radius: 5px;
            margin-bottom: 20px;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <!-- Sidebar + layout -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <div class="container-fluid">
        <div class="contract-form-container">
            <h1 class="page-title">Create New Contract</h1>
            
            <c:if test="${fromOrder == true}">
                <div class="info-badge">
                    <i class="fas fa-info-circle me-2"></i>
                    This contract is being created from an Order. Fields are pre-filled from the Order.
                </div>
            </c:if>
            
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
            
            <form action="${contractsBase}" method="POST" id="createContractForm" autocomplete="off">
                <input type="hidden" name="action" value="create">
                <c:if test="${not empty orderId}">
                    <input type="hidden" name="orderId" value="${orderId}">
                </c:if>
                
                <!-- Contract Code -->
                <div class="mb-3">
                    <label for="contractCode" class="form-label">Mã hợp đồng <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="contractCode" name="contractCode" 
                           value="${contractCode != null ? contractCode : ''}" required readonly>
                    <small class="form-text text-muted">Auto-generated contract code</small>
                </div>
                
                <!-- Customer Name -->
                <div class="mb-3">
                    <label for="customerName" class="form-label">Tên khách hàng <span class="text-danger">*</span></label>
                    <input type="text" class="form-control" id="customerName" name="customerName" 
                           value="${customerName != null ? customerName : ''}" required>
                </div>
                
                <!-- Customer Phone -->
                <div class="mb-3">
                    <label for="customerPhone" class="form-label">Số điện thoại</label>
                    <input type="tel" class="form-control" id="customerPhone" name="customerPhone" 
                           value="${customerPhone != null ? customerPhone : ''}">
                </div>
                
                <!-- Customer Address -->
                <div class="mb-3">
                    <label for="customerAddress" class="form-label">Địa chỉ</label>
                    <textarea class="form-control" id="customerAddress" name="customerAddress" rows="2"
                              placeholder="Enter customer address...">${customerAddress != null ? customerAddress : ''}</textarea>
                </div>
                
                <!-- Customer ID (hidden dropdown for linking to user account) -->
                <div class="mb-3">
                    <label for="customerId" class="form-label">Customer Account <span class="text-danger">*</span></label>
                    <select class="form-select" id="customerId" name="customerId" required>
                        <option value="">Select Customer Account</option>
                        <c:forEach var="customer" items="${customers}">
                            <option value="${customer.id}" ${selectedCustomerId != null && selectedCustomerId == customer.id ? 'selected' : ''}>
                                ${customer.fullName != null ? customer.fullName : customer.username} (${customer.email})
                            </option>
                        </c:forEach>
                    </select>
                    <small class="form-text text-muted">Select the customer account to link this contract</small>
                </div>
                
                <!-- Machine Type -->
                <div class="mb-3">
                    <label for="machineTypeId" class="form-label">Loại máy</label>
                    <select class="form-select" id="machineTypeId" name="machineTypeId">
                        <option value="">-- Select Machine Type --</option>
                        <c:forEach var="machineType" items="${machineTypes}">
                            <option value="${machineType.id}" ${machineTypeId != null && machineTypeId == machineType.id ? 'selected' : ''}>
                                ${machineType.typeName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <!-- Quantity -->
                <div class="mb-3">
                    <label for="quantity" class="form-label">Số lượng</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" 
                           value="${quantity != null ? quantity : ''}" min="1" step="1">
                </div>
                
                <!-- Manager -->
                <div class="mb-3">
                    <label for="managerId" class="form-label">Manager <span class="text-danger">*</span></label>
                    <select class="form-select" id="managerId" name="managerId" required>
                        <option value="">Select Manager</option>
                        <c:forEach var="manager" items="${managers}">
                            <option value="${manager.id}" 
                                    ${(selectedManagerId != null && selectedManagerId == manager.id) || (defaultManagerId != null && defaultManagerId == manager.id) ? 'selected' : ''}>
                                ${manager.fullName != null ? manager.fullName : manager.username} (${manager.email}) - ${manager.roleName}
                            </option>
                        </c:forEach>
                    </select>
                </div>
                
                <!-- Start Date & End Date -->
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label for="startDate" class="form-label">Ngày bắt đầu (thuê) <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="startDate" name="startDate" 
                               value="${startDate != null ? startDate : ''}" required autocomplete="off">
                    </div>
                    
                    <div class="col-md-6 mb-3">
                        <label for="endDate" class="form-label">Ngày kết thúc (thuê) <span class="text-danger">*</span></label>
                        <input type="date" class="form-control" id="endDate" name="endDate" 
                               value="${endDate != null ? endDate : ''}" required autocomplete="off">
                    </div>
                </div>
                
                <!-- Total Cost -->
                <div class="mb-3">
                    <label for="totalCost" class="form-label">Tổng giá trị</label>
                    <input type="number" class="form-control" id="totalCost" name="totalCost" 
                           value="${totalCost != null ? totalCost : ''}" min="0" step="0.01" placeholder="0.00">
                </div>
                
                <!-- Note -->
                <div class="mb-3">
                    <label for="note" class="form-label">Ghi chú</label>
                    <textarea class="form-control" id="note" name="note" rows="3" 
                              placeholder="Enter any additional notes...">${note != null ? note : ''}</textarea>
                </div>
                
                <div class="btn-group-custom">
                    <a href="${contractsBase}" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <button type="submit" class="btn btn-add">
                        <i class="fas fa-save"></i> Create Contract
                    </button>
                </div>
            </form>
        </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        const customerSelect = document.getElementById('customerId');
        const managerSelect = document.getElementById('managerId');
        
        // Validate date range
        document.getElementById('startDate').addEventListener('change', validateDates);
        document.getElementById('endDate').addEventListener('change', validateDates);
        
        function validateDates() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            
            if (startDate && endDate && startDate > endDate) {
                alert('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!');
                document.getElementById('endDate').value = '';
            }
        }
        
        // Form validation on submit
        document.getElementById('createContractForm').addEventListener('submit', function(e) {
            const contractCode = document.getElementById('contractCode').value.trim();
            const customerName = document.getElementById('customerName').value.trim();
            const customerId = customerSelect.value;
            const managerId = managerSelect.value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            
            // Check required fields
            if (!contractCode || !customerName || !customerId || !managerId || !startDate || !endDate) {
                e.preventDefault();
                alert('Vui lòng điền đầy đủ các trường bắt buộc!');
                return false;
            }
            
            // Check customer and manager are different
            if (customerId && managerId && customerId === managerId) {
                e.preventDefault();
                alert('Customer và Manager không được là cùng một người!');
                return false;
            }
            
            // Check date range
            if (startDate > endDate) {
                e.preventDefault();
                alert('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!');
                return false;
            }
        });
    </script>
</body>
</html>
