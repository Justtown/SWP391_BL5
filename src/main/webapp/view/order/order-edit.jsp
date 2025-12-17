<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa đơn hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-4">
    <div class="card shadow-sm">
        <div class="card-header bg-warning text-dark">
            <h4 class="mb-0"><i class="bi bi-pencil-square"></i> Chỉnh sửa đơn hàng #${order.id}</h4>
        </div>
        <div class="card-body">
            <c:if test="${order.status != 'PENDING'}">
                <div class="alert alert-warning" role="alert">
                    <i class="bi bi-exclamation-triangle"></i> 
                    <strong>Cảnh báo:</strong> Đơn hàng này đã được ${order.status}. Không thể chỉnh sửa!
                </div>
                <a href="${pageContext.request.contextPath}/sale/orders?action=list" class="btn btn-secondary">
                    <i class="bi bi-arrow-left"></i> Quay lại
                </a>
            </c:if>

            <c:if test="${order.status == 'PENDING'}">
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
                    }
                    // Lấy machineTypeId và quantity từ request nếu có
                    String editMachineTypeId = (String) request.getAttribute("machineTypeId");
                    String editQuantity = (String) request.getAttribute("quantity");
                %>
                
                <form action="${pageContext.request.contextPath}/sale/orders" method="post" id="orderForm">
                    <input type="hidden" name="action" value="update"/>
                    <input type="hidden" name="id" value="${order.id}"/>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Mã hợp đồng <span class="text-danger">*</span></label>
                            <input type="text" name="contractCode" class="form-control" 
                                   value="${order.contractCode}" required 
                                   pattern="[A-Za-z0-9]+" 
                                   title="Chỉ chứa chữ và số">
                            <div class="form-text">Mã duy nhất cho hợp đồng này</div>
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Tên khách hàng <span class="text-danger">*</span></label>
                            <input type="text" name="customerName" class="form-control" 
                                   value="${order.customerName}" required>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Số điện thoại</label>
                            <input type="tel" name="customerPhone" class="form-control" 
                                   value="${order.customerPhone}"
                                   pattern="[0-9]{10,11}"
                                   title="Số điện thoại 10-11 số">
                        </div>

                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Địa chỉ khách hàng</label>
                            <input type="text" name="customerAddress" class="form-control" 
                                   value="${order.customerAddress}">
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-8 mb-3">
                            <label class="form-label fw-bold">Tên loại máy <span class="text-danger">*</span></label>
                            <select name="machineTypeId" class="form-control" required>
                                <option value="">-- Chọn loại máy --</option>
                                <c:forEach var="type" items="${machineTypes}">
                                    <c:choose>
                                        <c:when test="${not empty requestScope.machineTypeId}">
                                            <option value="${type.id}" ${type.id == requestScope.machineTypeId ? 'selected' : ''}>
                                                ${type.id} - ${type.typeName}
                                            </option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${type.id}" ${type.id == order.machineId ? 'selected' : ''}>
                                                ${type.id} - ${type.typeName}
                                            </option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-4 mb-3">
                            <label class="form-label fw-bold">Số lượng <span class="text-danger">*</span></label>
                            <input type="number" name="quantity" class="form-control" 
                                   value="${not empty requestScope.quantity ? requestScope.quantity : order.quantity}" 
                                   min="1" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Mô tả dịch vụ <span class="text-danger">*</span></label>
                        <textarea name="serviceDescription" class="form-control" rows="4" required>${order.serviceDescription}</textarea>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Ngày bắt đầu <span class="text-danger">*</span></label>
                            <input type="date" name="startDate" class="form-control" 
                                   value="${order.startDate}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Ngày kết thúc dự kiến</label>
                            <input type="date" name="endDate" class="form-control" 
                                   value="${order.endDate}">
                            <div class="form-text">Ngày dự kiến hoàn thành</div>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold">Tổng giá trị hợp đồng (VNĐ)</label>
                        <input type="number" step="1000" name="totalCost" class="form-control" 
                               value="${order.totalCost}" min="0">
                        <div class="form-text">Giá trị ước tính hoặc đã thỏa thuận</div>
                    </div>

                    <div class="alert alert-info" role="alert">
                        <i class="bi bi-info-circle"></i> 
                        <strong>Lưu ý:</strong> Sau khi chỉnh sửa, đơn hàng vẫn ở trạng thái "Chờ duyệt" và cần được Admin phê duyệt.
                    </div>

                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/sale/orders?action=list" class="btn btn-secondary">
                            <i class="bi bi-arrow-left"></i> Hủy
                        </a>
                        <button type="submit" class="btn btn-warning">
                            <i class="bi bi-check-circle"></i> Cập nhật
                        </button>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</div>

<script>
// Validation: End date must be after start date
document.getElementById('orderForm')?.addEventListener('submit', function(e) {
    const startDate = document.querySelector('[name="startDate"]').value;
    const endDate = document.querySelector('[name="endDate"]').value;
    
    if (endDate && startDate && new Date(endDate) < new Date(startDate)) {
        e.preventDefault();
        alert('Ngày kết thúc phải sau ngày bắt đầu!');
    }
});
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
