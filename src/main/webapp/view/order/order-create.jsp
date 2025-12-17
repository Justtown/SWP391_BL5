<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo đơn hàng mới - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid">
<div class="container mt-4">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0"><i class="bi bi-plus-square"></i> Tạo đơn hàng mới (Hợp đồng gia công)</h4>
        </div>
        <div class="card-body">
            <%
                String error = (String) request.getAttribute("error");
                if (error == null) error = request.getParameter("error");
                if ("duplicate_contract".equals(error)) {
            %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    <strong>Lỗi!</strong> Mã hợp đồng này đã tồn tại. Vui lòng sử dụng mã khác.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <%
                } else if ("invalid_customer".equals(error)) {
            %>
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                    <strong>Lỗi!</strong> Tên khách hàng không có trong hệ thống. Vui lòng chọn từ danh sách gợi ý.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <%
                }
                // Lấy các giá trị đã nhập (nếu có)
                String contractCode = (String) request.getAttribute("contractCode");
                String customerName = (String) request.getAttribute("customerName");
                String customerPhone = (String) request.getAttribute("customerPhone");
                String customerAddress = (String) request.getAttribute("customerAddress");
                String machineTypeId = (String) request.getAttribute("machineTypeId");
                String quantity = (String) request.getAttribute("quantity");
                String serviceDescription = (String) request.getAttribute("serviceDescription");
                String startDate = (String) request.getAttribute("startDate");
                String endDate = (String) request.getAttribute("endDate");
                String totalCost = (String) request.getAttribute("totalCost");
            %>
            
            <form action="${pageContext.request.contextPath}/sale/orders" method="post" id="orderForm">
                <input type="hidden" name="action" value="create"/>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Mã hợp đồng <span class="text-danger">*</span></label>
                        <input type="text" name="contractCode" class="form-control" 
                               placeholder="VD: HD001" required 
                               pattern="[A-Za-z0-9]+" 
                               title="Chỉ chứa chữ và số"
                               value="<%= contractCode != null ? contractCode : "" %>">
                        <div class="form-text">Mã duy nhất cho hợp đồng này</div>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên khách hàng <span class="text-danger">*</span></label>
                        <input type="text" name="customerName" class="form-control" 
                               placeholder="Nhập tên khách hàng..." required
                               list="customerList" id="customerInput"
                               value="<%= customerName != null ? customerName : "" %>">
                        <datalist id="customerList">
                            <c:forEach var="customer" items="${customers}">
                                <option value="${customer.username}" 
                                        data-fullname="${customer.fullName}"
                                        data-phone="${customer.phoneNumber}" 
                                        data-address="${customer.address}">
                                    ${customer.fullName}
                                </option>
                            </c:forEach>
                        </datalist>
                        <div class="form-text">Nhập để tìm kiếm khách hàng</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Số điện thoại</label>
                        <input type="tel" name="customerPhone" class="form-control" 
                               placeholder="VD: 0901234567"
                               pattern="[0-9]{10,11}"
                               title="Số điện thoại 10-11 số"
                               id="customerPhone"
                               value="<%= customerPhone != null ? customerPhone : "" %>"
                               readonly>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Địa chỉ khách hàng</label>
                        <input type="text" name="customerAddress" class="form-control" 
                               placeholder="VD: 123 Đường ABC, Q1, HCM"
                               id="customerAddress"
                               value="<%= customerAddress != null ? customerAddress : "" %>"
                               readonly>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-8 mb-3">
                        <label class="form-label fw-bold">Tên loại máy <span class="text-danger">*</span></label>
                        <select name="machineTypeId" class="form-control" required id="machineTypeSelect">
                            <option value="">-- Chọn loại máy --</option>
                            <c:forEach var="type" items="${machineTypes}">
                                <option value="${type.id}">${type.id} - ${type.typeName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-4 mb-3">
                        <label class="form-label fw-bold">Số lượng <span class="text-danger">*</span></label>
                        <input type="number" name="quantity" class="form-control" 
                               value="<%= quantity != null ? quantity : "1" %>" min="1" required>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Mô tả dịch vụ <span class="text-danger">*</span></label>
                    <textarea name="serviceDescription" class="form-control" rows="4" required
                              placeholder="Mô tả chi tiết về dịch vụ gia công, yêu cầu kỹ thuật, v.v."><%= serviceDescription != null ? serviceDescription : "" %></textarea>
                </div>
                <% if (machineTypeId != null) { %>
                <script>
                    document.addEventListener('DOMContentLoaded', function() {
                        document.getElementById('machineTypeSelect').value = '<%= machineTypeId %>';
                    });
                </script>
                <% } %>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ngày bắt đầu <span class="text-danger">*</span></label>
                        <input type="date" name="startDate" class="form-control" required 
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                               value="<%= startDate != null ? startDate : "" %>">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ngày kết thúc dự kiến</label>
                        <input type="date" name="endDate" class="form-control" 
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>"
                               value="<%= endDate != null ? endDate : "" %>">
                        <div class="form-text">Ngày dự kiến hoàn thành</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Tổng giá trị hợp đồng (VNĐ)</label>
                    <input type="number" step="1000" name="totalCost" class="form-control" 
                           placeholder="VD: 50000000" min="0"
                           value="<%= totalCost != null ? totalCost : "" %>">
                    <div class="form-text">Giá trị ước tính hoặc đã thỏa thuận</div>
                </div>

                <div class="alert alert-info" role="alert">
                    <i class="bi bi-info-circle"></i> 
                    <strong>Lưu ý:</strong> Đơn hàng sau khi tạo sẽ ở trạng thái "Chờ duyệt" và cần được Admin phê duyệt trước khi xử lý.
                </div>

                <div class="d-flex justify-content-end gap-2">
                    <a href="${pageContext.request.contextPath}/sale/orders?action=list" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> Quay lại
                    </a>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check-circle"></i> Tạo đơn hàng
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
// Build customer data map
const customerData = new Map();
<c:forEach var="customer" items="${customers}">
    customerData.set('${customer.username}', {
        fullName: '${customer.fullName}',
        phone: '${customer.phoneNumber}' || '',
        address: '${customer.address}' || ''
    });
</c:forEach>

// Auto-fill customer info when typing/selecting
const customerInput = document.getElementById('customerInput');
const phoneInput = document.getElementById('customerPhone');
const addressInput = document.getElementById('customerAddress');

customerInput.addEventListener('input', function() {
    const username = this.value.trim();
    if (customerData.has(username)) {
        const customer = customerData.get(username);
        phoneInput.value = customer.phone;
        addressInput.value = customer.address;
    }
});

customerInput.addEventListener('blur', function() {
    const username = this.value.trim();
    if (customerData.has(username)) {
        const customer = customerData.get(username);
        phoneInput.value = customer.phone;
        addressInput.value = customer.address;
    }
});

// Validation: End date must be after start date
document.getElementById('orderForm').addEventListener('submit', function(e) {
    const startDate = document.querySelector('[name="startDate"]').value;
    const endDate = document.querySelector('[name="endDate"]').value;
    
    if (endDate && startDate && new Date(endDate) < new Date(startDate)) {
        e.preventDefault();
        alert('Ngày kết thúc phải sau ngày bắt đầu!');
    }
});
</script>
</div>
</div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

