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
                    <input type="text" class="form-control" id="customerInput" name="customerName"
                           placeholder="Nhập username khách hàng..."
                           list="customerList"
                           value="${customerName != null ? customerName : ''}" required autocomplete="off">
                    <datalist id="customerList">
                        <c:forEach var="customer" items="${customers}">
                            <option value="${customer.username}"
                                    data-id="${customer.id}"
                                    data-phone="${customer.phoneNumber}"
                                    data-address="${customer.address}">
                                ${customer.fullName}
                            </option>
                        </c:forEach>
                    </datalist>
                    <input type="hidden" id="customerId" name="customerId" value="${selectedCustomerId != null ? selectedCustomerId : ''}">
                </div>
                
                <!-- Customer Phone -->
                <div class="mb-3">
                    <label for="customerPhone" class="form-label">Số điện thoại</label>
                    <input type="tel" class="form-control" id="customerPhone" name="customerPhone" 
                           value="${customerPhone != null ? customerPhone : ''}" readonly>
                </div>
                
                <!-- Customer Address -->
                <div class="mb-3">
                    <label for="customerAddress" class="form-label">Địa chỉ</label>
                    <textarea class="form-control" id="customerAddress" name="customerAddress" rows="2"
                              placeholder="Địa chỉ khách hàng..." readonly>${customerAddress != null ? customerAddress : ''}</textarea>
                </div>

                <!-- Machine -->
                <div class="row">
                    <div class="col-md-4 mb-3">
                        <label class="form-label">Mã máy <span class="text-danger">*</span></label>
                        <select name="machineId" class="form-select" required id="machineSelect">
                            <option value="">-- Chọn mã máy --</option>
                            <c:forEach var="machine" items="${machines}">
                                <option value="${machine.id}"
                                        ${machineId != null && machineId == machine.id ? 'selected' : ''}
                                        data-name="${machine.machineName}"
                                        data-type="${machine.machineTypeName}"
                                        data-status="${machine.status}"
                                        data-rentable="${machine.isRentable}"
                                        ${(machine.status ne 'ACTIVE' || machine.isRentable ne true) ? 'disabled' : ''}>
                                    ${machine.machineCode} (${machine.status})
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label">Tên máy</label>
                        <input type="text" id="machineNameDisplay" class="form-control" readonly
                               placeholder="Tự động điền khi chọn mã máy">
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label">Loại máy</label>
                        <input type="text" id="machineTypeDisplay" class="form-control" readonly
                               placeholder="Tự động điền khi chọn mã máy">
                    </div>
                </div>
                
                <!-- Quantity -->
                <div class="mb-3">
                    <label for="quantity" class="form-label">Số lượng</label>
                    <input type="number" class="form-control" id="quantity" name="quantity" 
                           value="${quantity != null ? quantity : '1'}" min="1" step="1" required>
                </div>

                <!-- ManagerId (auto for manager/sale; admin can choose) -->
                <c:choose>
                    <c:when test="${sessionScope.roleName == 'admin'}">
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
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="managerId" value="${sessionScope.userId}">
                    </c:otherwise>
                </c:choose>
                
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
                           value="${totalCost != null ? totalCost : ''}" min="1000000" step="1000" placeholder="VD: 5000000" required>
                    <div class="form-text text-danger">* Giá trị phải từ 1,000,000 VNĐ trở lên</div>
                </div>
                
                <!-- Service Description -->
                <div class="mb-3">
                    <label for="serviceDescription" class="form-label">Mô tả dịch vụ <span class="text-danger">*</span></label>
                    <textarea class="form-control" id="serviceDescription" name="serviceDescription" rows="4" required
                              placeholder="Mô tả chi tiết về dịch vụ...">${serviceDescription != null ? serviceDescription : (note != null ? note : '')}</textarea>
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
        // ===== Customer auto-fill (match Order create) =====
        const customerData = new Map();
        <c:forEach var="customer" items="${customers}">
        customerData.set('${customer.username}', {
            id: '${customer.id}',
            phone: '${customer.phoneNumber}' || '',
            address: '${customer.address}' || ''
        });
        </c:forEach>

        const customerInput = document.getElementById('customerInput');
        const customerIdHidden = document.getElementById('customerId');
        const phoneInput = document.getElementById('customerPhone');
        const addressInput = document.getElementById('customerAddress');

        function fillCustomer() {
            const username = (customerInput.value || '').trim();
            if (customerData.has(username)) {
                const c = customerData.get(username);
                customerIdHidden.value = c.id;
                phoneInput.value = c.phone || '';
                addressInput.value = c.address || '';
            } else {
                customerIdHidden.value = '';
                phoneInput.value = '';
                addressInput.value = '';
            }
        }

        customerInput.addEventListener('input', fillCustomer);
        customerInput.addEventListener('blur', fillCustomer);
        fillCustomer();

        // ===== Machine auto-fill =====
        const machineSelect = document.getElementById('machineSelect');
        const machineNameDisplay = document.getElementById('machineNameDisplay');
        const machineTypeDisplay = document.getElementById('machineTypeDisplay');

        function fillMachine() {
            const opt = machineSelect.options[machineSelect.selectedIndex];
            if (opt && opt.value) {
                machineNameDisplay.value = opt.getAttribute('data-name') || '';
                machineTypeDisplay.value = opt.getAttribute('data-type') || '';
            } else {
                machineNameDisplay.value = '';
                machineTypeDisplay.value = '';
            }
        }
        machineSelect.addEventListener('change', fillMachine);
        fillMachine();

        // ===== Validations =====
        const managerSelect = document.getElementById('managerId'); // only exists for admin

        document.getElementById('startDate').addEventListener('change', validateDates);
        document.getElementById('endDate').addEventListener('change', validateDates);
        machineSelect.addEventListener('change', validateMachineSelection);

        // Start date must be today or later
        (function initDateMins() {
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            const yyyy = today.getFullYear();
            const mm = String(today.getMonth() + 1).padStart(2, '0');
            const dd = String(today.getDate()).padStart(2, '0');
            const todayStr = `${yyyy}-${mm}-${dd}`;
            const startEl = document.getElementById('startDate');
            const endEl = document.getElementById('endDate');
            startEl.min = todayStr;
            if (endEl) {
                // endDate min will be adjusted inside validateDates() too
                endEl.min = startEl.value || todayStr;
            }
        })();

        function validateDates() {
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const endEl = document.getElementById('endDate');

            // enforce startDate >= today
            if (startDate) {
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                const s = new Date(startDate + 'T00:00:00');
                if (s < today) {
                    alert('Ngày bắt đầu thuê phải từ hôm nay trở đi!');
                    document.getElementById('startDate').value = '';
                    if (endEl) endEl.value = '';
                    return;
                }
                if (endEl) endEl.min = startDate;
            }

            if (startDate && endDate && startDate > endDate) {
                alert('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!');
                document.getElementById('endDate').value = '';
            }
        }

        function validateMachineSelection() {
            const opt = machineSelect.options[machineSelect.selectedIndex];
            if (!opt || !opt.value) {
                machineSelect.setCustomValidity('');
                return true;
            }
            const status = (opt.getAttribute('data-status') || '').toUpperCase();
            const rentableRaw = (opt.getAttribute('data-rentable') || '').toLowerCase();
            const rentable = rentableRaw === 'true';
            if (status !== 'ACTIVE' || !rentable) {
                const msg = `Máy đang ở trạng thái ${status || 'UNKNOWN'} (rentable=${rentable}) nên không thể tạo hợp đồng!`;
                alert(msg);
                machineSelect.value = '';
                fillMachine();
                machineSelect.setCustomValidity(msg);
                return false;
            }
            machineSelect.setCustomValidity('');
            return true;
        }

        const totalCostInput = document.getElementById('totalCost');
        if (totalCostInput) {
            totalCostInput.addEventListener('input', function() {
                const value = parseFloat(this.value);
                if (isNaN(value) || value < 1000000) {
                    this.setCustomValidity('Giá trị hợp đồng phải từ 1,000,000 VNĐ trở lên!');
                } else {
                    this.setCustomValidity('');
                }
            });
        }

        document.getElementById('createContractForm').addEventListener('submit', function(e) {
            const contractCode = document.getElementById('contractCode').value.trim();
            const customerUsername = customerInput.value.trim();
            const customerId = customerIdHidden.value;
            const managerId = managerSelect ? managerSelect.value : '${sessionScope.userId}';
            const machineId = machineSelect.value;
            const quantity = document.getElementById('quantity').value;
            const startDate = document.getElementById('startDate').value;
            const endDate = document.getElementById('endDate').value;
            const serviceDescription = document.getElementById('serviceDescription').value.trim();
            const totalCost = parseFloat(totalCostInput.value);

            if (!contractCode || !customerUsername || !customerId || !managerId || !machineId || !quantity || !startDate || !endDate || !serviceDescription) {
                e.preventDefault();
                alert('Vui lòng điền đầy đủ các trường bắt buộc!');
                return false;
            }

            if (customerId && managerId && customerId === managerId) {
                e.preventDefault();
                alert('Customer và Manager không được là cùng một người!');
                return false;
            }

            if (startDate > endDate) {
                e.preventDefault();
                alert('Ngày kết thúc phải sau hoặc bằng ngày bắt đầu!');
                return false;
            }

            // enforce startDate >= today
            if (startDate) {
                const today = new Date();
                today.setHours(0, 0, 0, 0);
                const s = new Date(startDate + 'T00:00:00');
                if (s < today) {
                    e.preventDefault();
                    alert('Ngày bắt đầu thuê phải từ hôm nay trở đi!');
                    return false;
                }
            }

            if (!validateMachineSelection()) {
                e.preventDefault();
                return false;
            }

            if (isNaN(totalCost) || totalCost < 1000000) {
                e.preventDefault();
                alert('Giá trị hợp đồng phải từ 1,000,000 VNĐ trở lên!');
                totalCostInput.focus();
                return false;
            }
        });
    </script>
</body>
</html>
