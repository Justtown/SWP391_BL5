<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Bảo Trì Máy - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f8f9fa; }
        .main-content { transition: all 0.3s; }
        .page-header { background: white; border-bottom: 1px solid #dee2e6; padding: 1rem 1.5rem; margin-bottom: 1.5rem; }
        .content-card { background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); padding: 1.5rem; margin-bottom: 1.5rem; }
        .stats-card { background: white; border-radius: 12px; padding: 1.25rem; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
        .stats-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; }
        .stats-icon.primary { background: rgba(13,110,253,0.1); color: #0d6efd; }
        .stats-icon.success { background: rgba(25,135,84,0.1); color: #198754; }
        .stats-icon.warning { background: rgba(255,193,7,0.15); color: #cc9a06; }
        .stats-value { font-size: 1.75rem; font-weight: 700; }
        .stats-label { color: #6c757d; font-size: 0.875rem; }
        .modal-card { border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); border: none; }
        .filter-section { background: #f8f9fa; padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem; border: 1px solid #eee; }
        .table thead th { background-color: #f8f9fa; text-transform: uppercase; font-size: 0.75rem; letter-spacing: 0.5px; color: #6c757d; }
    </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />

<div class="main-content">
    <div class="page-header d-flex justify-content-between align-items-center">
        <div>
            <h4 class="mb-1"><i class="fas fa-tools me-2 text-primary"></i>Quản Lý Bảo Trì Máy</h4>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard" class="text-decoration-none">Dashboard</a></li>
                    <li class="breadcrumb-item active">Bảo Trì</li>
                </ol>
            </nav>
        </div>
    </div>

    <div class="container-fluid">
        <c:if test="${not empty successMsg}">
            <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
                <i class="fas fa-check-circle me-2"></i> ${successMsg}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty errorMsg}">
            <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm" role="alert">
                <i class="fas fa-exclamation-circle me-2"></i> ${errorMsg}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="row mb-4">
            <div class="col-md-4 mb-3">
                <div class="stats-card">
                    <div class="d-flex align-items-center">
                        <div class="stats-icon primary me-3"><i class="fas fa-wrench"></i></div>
                        <div>
                            <div class="stats-value">${totalCount}</div>
                            <div class="stats-label">Tổng bảo trì</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-3">
                <div class="stats-card">
                    <div class="d-flex align-items-center">
                        <div class="stats-icon success me-3"><i class="fas fa-check-circle"></i></div>
                        <div>
                            <div class="stats-value">${completedCount}</div>
                            <div class="stats-label">Hoàn thành</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-4 mb-3">
                <div class="stats-card">
                    <div class="d-flex align-items-center">
                        <div class="stats-icon warning me-3"><i class="fas fa-clock"></i></div>
                        <div>
                            <div class="stats-value">${pendingCount}</div>
                            <div class="stats-label">Đang chờ</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="content-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h5 class="mb-0 fw-bold">Danh sách bảo trì</h5>
                <button type="button" class="btn btn-primary px-4 shadow-sm" data-bs-toggle="modal" data-bs-target="#createModal">
                    <i class="fas fa-plus me-2"></i> Thêm bảo trì
                </button>
            </div>

            <div class="filter-section">
                <form method="GET" action="${pageContext.request.contextPath}/manager/maintenances">
                    <div class="row g-3">
                        <div class="col-md-3">
                            <label class="small text-muted mb-1">Theo máy</label>
                            <select class="form-select border-0 shadow-sm" name="machineId">
                                <option value="">Tất cả máy</option>
                                <c:forEach var="machine" items="${machines}">
                                    <option value="${machine.id}" ${filterMachineId == machine.id ? 'selected' : ''}>
                                            ${machine.machineCode} - ${machine.machineName}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="small text-muted mb-1">Loại bảo trì</label>
                            <select class="form-select border-0 shadow-sm" name="maintenanceType">
                                <option value="">Tất cả loại</option>
                                <c:forEach var="type" items="${maintenanceTypes}">
                                    <option value="${type}" ${filterType == type ? 'selected' : ''}>${type}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <label class="small text-muted mb-1">Trạng thái</label>
                            <select class="form-select border-0 shadow-sm" name="status">
                                <option value="">Tất cả trạng thái</option>
                                <option value="COMPLETED" ${filterStatus == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                <option value="PENDING" ${filterStatus == 'PENDING' ? 'selected' : ''}>Đang chờ</option>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex align-items-end">
                            <button type="submit" class="btn btn-outline-primary w-100 shadow-sm">
                                <i class="fas fa-filter me-2"></i> Lọc dữ liệu
                            </button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th class="text-center" style="width: 50px;">#</th>
                        <th>Thông tin máy</th>
                        <th>Loại</th>
                        <th>Ngày bảo trì</th>
                        <th>Người thực hiện</th>
                        <th>Trạng thái</th>
                        <th class="text-center" style="width: 150px;">Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty maintenances}">
                            <c:forEach var="m" items="${maintenances}" varStatus="loop">
                                <tr>
                                    <td class="text-center text-muted">${(currentPage - 1) * 4 + loop.index + 1}</td>
                                    <td>
                                        <div class="fw-bold text-dark">${m.machineCode}</div>
                                        <div class="small text-muted">${m.machineName}</div>
                                    </td>
                                    <td><span class="text-secondary">${m.maintenanceType}</span></td>
                                    <td><fmt:formatDate value="${m.maintenanceDate}" pattern="dd/MM/yyyy"/></td>
                                    <td>${m.performedBy != null ? m.performedBy : '<span class="text-muted italic">Chưa rõ</span>'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${m.status == 'COMPLETED'}">
                                                <span class="badge bg-success-subtle text-success border border-success-subtle px-3">Hoàn thành</span>
                                            </c:when>
                                            <c:when test="${m.status == 'PENDING'}">
                                                <span class="badge bg-warning-subtle text-warning-emphasis border border-warning-subtle px-3">Đang chờ</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-light text-dark border px-3">${m.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td class="text-center">
                                        <div class="btn-group shadow-sm">
                                            <button class="btn btn-sm btn-outline-info btn-view" data-id="${m.id}" title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </button>
                                            <c:choose>
                                                <c:when test="${m.status == 'COMPLETED'}">
                                                    <button class="btn btn-sm btn-outline-secondary" disabled title="Đã hoàn thành">
                                                        <i class="fas fa-lock"></i>
                                                    </button>
                                                </c:when>
                                                <c:otherwise>
                                                    <button class="btn btn-sm btn-outline-warning btn-edit" data-id="${m.id}" title="Chỉnh sửa">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                </c:otherwise>
                                            </c:choose>
                                            <button class="btn btn-sm btn-outline-danger btn-delete" data-id="${m.id}" title="Xóa">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center text-muted py-5">
                                    <img src="https://cdn-icons-png.flaticon.com/512/7486/7486744.png" width="60" class="opacity-25 mb-3"><br>
                                    Không tìm thấy dữ liệu bảo trì
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>

            <c:if test="${totalPages > 1}">
                <nav class="mt-4">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link shadow-none" href="?page=${currentPage - 1}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">
                                <i class="fas fa-chevron-left small"></i>
                            </a>
                        </li>
                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                    <a class="page-link shadow-none" href="?page=${i}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">${i}</a>
                                </li>
                            </c:if>
                            <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                <li class="page-item disabled"><span class="page-link">...</span></li>
                            </c:if>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                            <a class="page-link shadow-none" href="?page=${currentPage + 1}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">
                                <i class="fas fa-chevron-right small"></i>
                            </a>
                        </li>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>

<div class="modal fade" id="createModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content modal-card">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><i class="fas fa-plus-circle me-2"></i> Thêm bản ghi bảo trì</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-body p-4">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Máy móc <span class="text-danger">*</span></label>
                            <select class="form-select" name="machineId" required>
                                <option value="">-- Chọn máy --</option>
                                <c:forEach var="machine" items="${machines}">
                                    <option value="${machine.id}">${machine.machineCode} - ${machine.machineName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Loại bảo trì <span class="text-danger">*</span></label>
                            <select class="form-select" name="maintenanceType" required>
                                <option value="">-- Chọn loại --</option>
                                <c:forEach var="type" items="${maintenanceTypes}">
                                    <option value="${type}">${type}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Ngày thực hiện <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" name="maintenanceDate" id="createDate" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Người thực hiện</label>
                            <input type="text" class="form-control" name="performedBy" placeholder="Họ và tên">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Trạng thái ban đầu</label>
                            <select class="form-select" name="status">
                                <option value="PENDING" selected>Đang chờ thực hiện</option>
                                <option value="COMPLETED">Đã hoàn thành ngay</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-0">
                        <label class="form-label fw-semibold">Mô tả công việc / Ghi chú</label>
                        <textarea class="form-control" name="description" rows="3" placeholder="Nhập chi tiết nội dung bảo trì..."></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">Đóng</button>
                    <button type="submit" class="btn btn-primary px-4 shadow-sm">Lưu thông tin</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="viewModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content modal-card">
            <div class="modal-header bg-info text-white">
                <h5 class="modal-title"><i class="fas fa-info-circle me-2"></i> Chi tiết lịch sử bảo trì</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body p-4">
                <div class="row mb-4">
                    <div class="col-md-6 border-end">
                        <label class="text-uppercase small text-muted fw-bold mb-1">Thiết bị bảo trì</label>
                        <p id="viewMachine" class="fs-5 fw-bold text-dark mb-0"></p>
                        <span id="viewType" class="badge bg-primary-subtle text-primary mt-1"></span>
                    </div>
                    <div class="col-md-6 ps-4">
                        <label class="text-uppercase small text-muted fw-bold mb-1">Trạng thái hiện tại</label>
                        <div id="viewStatusContainer" class="mt-1"></div>
                    </div>
                </div>
                <hr class="opacity-50">
                <div class="row mb-4">
                    <div class="col-md-6">
                        <label class="text-muted small fw-bold mb-1"><i class="far fa-calendar-alt me-1"></i> Ngày thực hiện</label>
                        <p id="viewDate" class="fw-semibold"></p>
                    </div>
                    <div class="col-md-6">
                        <label class="text-muted small fw-bold mb-1"><i class="far fa-user me-1"></i> Nhân viên thực hiện</label>
                        <p id="viewPerformedBy" class="fw-semibold"></p>
                    </div>
                </div>
                <div class="bg-light p-3 rounded">
                    <label class="text-muted small fw-bold mb-2">Nội dung chi tiết</label>
                    <div id="viewDescription" style="white-space: pre-line; color: #444;"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary px-4" data-bs-dismiss="modal">Đóng</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="editModal" tabindex="-1">
    <div class="modal-dialog modal-lg">
        <div class="modal-content modal-card">
            <div class="modal-header bg-warning">
                <h5 class="modal-title fw-bold"><i class="fas fa-edit me-2"></i> Cập nhật thông tin bảo trì</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" id="editId">
                <div class="modal-body p-4">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Máy móc</label>
                            <select class="form-select" name="machineId" id="editMachineId" required>
                                <c:forEach var="machine" items="${machines}">
                                    <option value="${machine.id}">${machine.machineCode} - ${machine.machineName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Loại bảo trì</label>
                            <select class="form-select" name="maintenanceType" id="editType" required>
                                <c:forEach var="type" items="${maintenanceTypes}">
                                    <option value="${type}">${type}</option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Ngày thực hiện</label>
                            <input type="date" class="form-control" name="maintenanceDate" id="editDate" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold">Người thực hiện</label>
                            <input type="text" class="form-control" name="performedBy" id="editPerformedBy">
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-semibold text-primary">Cập nhật trạng thái</label>
                            <select class="form-select border-primary" name="status" id="editStatus">
                                <option value="PENDING">Đang chờ</option>
                                <option value="COMPLETED">Đã hoàn thành</option>
                            </select>
                        </div>
                    </div>
                    <div class="mb-0">
                        <label class="form-label fw-semibold">Ghi chú sửa đổi</label>
                        <textarea class="form-control" name="description" id="editDescription" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer bg-light">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy bỏ</button>
                    <button type="submit" class="btn btn-warning px-4 shadow-sm fw-bold">Cập nhật</button>
                </div>
            </form>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content modal-card">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title"><i class="fas fa-trash me-2"></i> Xác nhận xóa</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" id="deleteId">
                <div class="modal-body text-center p-4">
                    <div class="mb-3">
                        <i class="fas fa-exclamation-triangle fa-4x text-danger opacity-25"></i>
                    </div>
                    <p class="fs-5 mb-1">Bạn chắc chắn muốn xóa bản ghi này?</p>
                    <p class="text-muted small">Hành động này không thể hoàn tác dữ liệu trong lịch sử.</p>
                </div>
                <div class="modal-footer border-0 justify-content-center pb-4">
                    <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger px-4 shadow-sm">Xóa dữ liệu</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const ctx = '${pageContext.request.contextPath}';

        // Mặc định ngày tạo là hôm nay
        const createDateInput = document.getElementById('createDate');
        if(createDateInput) createDateInput.value = new Date().toISOString().split('T')[0];

        // Xử lý nạp dữ liệu cho View Modal
        document.querySelectorAll('.btn-view').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                fetch(ctx + '/manager/maintenances?action=detail&id=' + id)
                    .then(r => r.json())
                    .then(data => {
                        if (data.success) {
                            const m = data.maintenance;
                            document.getElementById('viewMachine').innerText = (m.machineCode || '') + " - " + (m.machineName || '');
                            document.getElementById('viewType').innerText = m.maintenanceType || '-';

                            // Format ngày
                            const d = new Date(m.maintenanceDate);
                            document.getElementById('viewDate').innerText = !isNaN(d.getTime()) ? d.toLocaleDateString('vi-VN') : '-';

                            document.getElementById('viewPerformedBy').innerText = m.performedBy || 'Chưa phân công';
                            document.getElementById('viewDescription').innerText = m.description || 'Không có ghi chú chi tiết.';

                            const statusContainer = document.getElementById('viewStatusContainer');
                            if (m.status === 'COMPLETED') {
                                statusContainer.innerHTML = '<span class="badge bg-success py-2 px-3"><i class="fas fa-check me-1"></i> HOÀN THÀNH</span>';
                            } else {
                                statusContainer.innerHTML = '<span class="badge bg-warning text-dark py-2 px-3"><i class="fas fa-clock me-1"></i> ĐANG CHỜ</span>';
                            }

                            new bootstrap.Modal(document.getElementById('viewModal')).show();
                        }
                    });
            });
        });

        // Xử lý nạp dữ liệu cho Edit Modal
        document.querySelectorAll('.btn-edit').forEach(btn => {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                fetch(ctx + '/manager/maintenances?action=detail&id=' + id)
                    .then(r => r.json())
                    .then(data => {
                        if (data.success) {
                            const m = data.maintenance;
                            document.getElementById('editId').value = m.id;
                            document.getElementById('editMachineId').value = m.machineId;
                            document.getElementById('editType').value = m.maintenanceType;

                            // Chuẩn hóa ngày cho input type="date"
                            let yyyyMmDd = '';
                            const rawDate = m.maintenanceDate;
                            const parsedDate = new Date(rawDate);
                            if (!isNaN(parsedDate.getTime())) {
                                yyyyMmDd = parsedDate.toISOString().split('T')[0];
                            }
                            document.getElementById('editDate').value = yyyyMmDd;

                            document.getElementById('editPerformedBy').value = m.performedBy || '';
                            document.getElementById('editStatus').value = m.status;
                            document.getElementById('editDescription').value = m.description || '';

                            new bootstrap.Modal(document.getElementById('editModal')).show();
                        }
                    });
            });
        });

        // Xử lý nạp dữ liệu cho Delete Modal
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', function() {
                document.getElementById('deleteId').value = this.getAttribute('data-id');
                new bootstrap.Modal(document.getElementById('deleteModal')).show();
            });
        });
    });
</script>
</body>
</html>