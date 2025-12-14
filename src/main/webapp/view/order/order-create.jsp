
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo đơn hàng mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-4">
    <div class="card shadow-sm">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0"><i class="bi bi-plus-square"></i> Tạo đơn hàng mới (Hợp đồng gia công)</h4>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/sale/orders" method="post" id="orderForm">
                <input type="hidden" name="action" value="create"/>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Mã hợp đồng <span class="text-danger">*</span></label>
                        <input type="text" name="contractCode" class="form-control" 
                               placeholder="VD: HD001" required 
                               pattern="[A-Za-z0-9]+" 
                               title="Chỉ chứa chữ và số">
                        <div class="form-text">Mã duy nhất cho hợp đồng này</div>
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên khách hàng <span class="text-danger">*</span></label>
                        <input type="text" name="customerName" class="form-control" 
                               placeholder="VD: Công ty ABC" required>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Số điện thoại</label>
                        <input type="tel" name="customerPhone" class="form-control" 
                               placeholder="VD: 0901234567"
                               pattern="[0-9]{10,11}"
                               title="Số điện thoại 10-11 số">
                    </div>

                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Địa chỉ khách hàng</label>
                        <input type="text" name="customerAddress" class="form-control" 
                               placeholder="VD: 123 Đường ABC, Q1, HCM">
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Machine ID (nếu có)</label>
                    <input type="number" name="machineId" class="form-control" 
                           placeholder="Nhập ID máy móc nếu có liên quan">
                    <div class="form-text">Chỉ điền nếu đơn hàng liên quan đến máy móc cụ thể</div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Mô tả dịch vụ <span class="text-danger">*</span></label>
                    <textarea name="serviceDescription" class="form-control" rows="4" required
                              placeholder="Mô tả chi tiết về dịch vụ gia công, yêu cầu kỹ thuật, v.v."></textarea>
                </div>

                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ngày bắt đầu <span class="text-danger">*</span></label>
                        <input type="date" name="startDate" class="form-control" required 
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ngày kết thúc dự kiến</label>
                        <input type="date" name="endDate" class="form-control" 
                               min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) %>">
                        <div class="form-text">Ngày dự kiến hoàn thành</div>
                    </div>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-bold">Tổng giá trị hợp đồng (VNĐ)</label>
                    <input type="number" step="1000" name="totalCost" class="form-control" 
                           placeholder="VD: 50000000" min="0">
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
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

