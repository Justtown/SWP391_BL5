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
        .page-header { background: white; border-bottom: 1px solid #dee2e6; padding: 1rem 1.5rem; margin-bottom: 1.5rem; }
        .content-card { background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); padding: 1.5rem; margin-bottom: 1.5rem; }
        .stats-card { background: white; border-radius: 12px; padding: 1.25rem; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
        .stats-icon { width: 48px; height: 48px; border-radius: 12px; display: flex; align-items: center; justify-content: center; font-size: 1.25rem; }
        .stats-icon.primary { background: rgba(13,110,253,0.1); color: #0d6efd; }
        .stats-icon.success { background: rgba(25,135,84,0.1); color: #198754; }
        .stats-icon.warning { background: rgba(255,193,7,0.15); color: #cc9a06; }
        .stats-value { font-size: 1.75rem; font-weight: 700; }
        .stats-label { color: #6c757d; font-size: 0.875rem; }
        .modal-card { border-radius: 10px; box-shadow: 0 10px 40px rgba(0,0,0,0.2); }
        .filter-section { background: #f8f9fa; padding: 1rem; border-radius: 8px; margin-bottom: 1.5rem; }
    </style>
</head>
<body>
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <div class="main-content">
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-tools me-2"></i>Quản Lý Bảo Trì Máy Trong Kho</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Bảo Trì</li>
                    </ol>
                </nav>
            </div>
        </div>
        
        <div class="container-fluid">
            <c:if test="${not empty successMsg}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> ${successMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle"></i> ${errorMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <!-- Statistics -->
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
                    <h5 class="mb-0">Danh sách bảo trì</h5>
                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">
                        <i class="fas fa-plus"></i> Thêm bảo trì
                    </button>
                </div>
                
                <!-- Filter -->
                <div class="filter-section">
                    <form method="GET" action="${pageContext.request.contextPath}/manager/maintenances">
                        <div class="row g-3">
                            <div class="col-md-3">
                                <select class="form-select" name="machineId">
                                    <option value="">Tất cả máy</option>
                                    <c:forEach var="machine" items="${machines}">
                                        <option value="${machine.id}" ${filterMachineId == machine.id ? 'selected' : ''}>
                                            ${machine.machineCode} - ${machine.machineName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <select class="form-select" name="maintenanceType">
                                    <option value="">Tất cả loại</option>
                                    <c:forEach var="type" items="${maintenanceTypes}">
                                        <option value="${type}" ${filterType == type ? 'selected' : ''}>${type}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <select class="form-select" name="status">
                                    <option value="">Tất cả trạng thái</option>
                                    <option value="COMPLETED" ${filterStatus == 'COMPLETED' ? 'selected' : ''}>Hoàn thành</option>
                                    <option value="PENDING" ${filterStatus == 'PENDING' ? 'selected' : ''}>Đang chờ</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <button type="submit" class="btn btn-outline-primary w-100">
                                    <i class="fas fa-filter"></i> Lọc
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
                
                <!-- Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Máy</th>
                                <th>Loại bảo trì</th>
                                <th>Ngày</th>
                                <th>Người thực hiện</th>
                                <th>Trạng thái</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty maintenances}">
                                    <c:forEach var="m" items="${maintenances}" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td>
                                                <strong>${m.machineCode}</strong><br>
                                                <small class="text-muted">${m.machineName}</small>
                                            </td>
                                            <td>${m.maintenanceType}</td>
                                            <td><fmt:formatDate value="${m.maintenanceDate}" pattern="dd/MM/yyyy"/></td>
                                            <td>${m.performedBy != null ? m.performedBy : '-'}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${m.status == 'COMPLETED'}">
                                                        <span class="badge bg-success">Hoàn thành</span>
                                                    </c:when>
                                                    <c:when test="${m.status == 'PENDING'}">
                                                        <span class="badge bg-warning text-dark">Đang chờ</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">${m.status}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <button class="btn btn-sm btn-warning btn-edit" data-id="${m.id}">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <button class="btn btn-sm btn-danger btn-delete" data-id="${m.id}">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="7" class="text-center text-muted py-4">
                                            <i class="fas fa-inbox fa-2x mb-2"></i><br>Chưa có bản ghi nào
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
                
                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <nav class="mt-4">
                        <ul class="pagination justify-content-center">
                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage - 1}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">
                                    <i class="fas fa-chevron-left"></i>
                                </a>
                            </li>
                            
                            <c:forEach var="i" begin="1" end="${totalPages}">
                                <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">${i}</a>
                                    </li>
                                </c:if>
                                <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                    <li class="page-item disabled"><span class="page-link">...</span></li>
                                </c:if>
                            </c:forEach>
                            
                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                <a class="page-link" href="?page=${currentPage + 1}&machineId=${filterMachineId}&maintenanceType=${filterType}&status=${filterStatus}">
                                    <i class="fas fa-chevron-right"></i>
                                </a>
                            </li>
                        </ul>
                        <div class="text-center text-muted">
                            <small>Trang ${currentPage} / ${totalPages} (Tổng: ${totalRecords} bản ghi)</small>
                        </div>
                    </nav>
                </c:if>
            </div>
        </div>
    </div>
    
    <!-- CREATE MODAL -->
    <div class="modal fade" id="createModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title"><i class="fas fa-plus-circle"></i> Thêm bảo trì mới</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Máy <span class="text-danger">*</span></label>
                                <select class="form-select" name="machineId" required>
                                    <option value="">-- Chọn máy --</option>
                                    <c:forEach var="machine" items="${machines}">
                                        <option value="${machine.id}">${machine.machineCode} - ${machine.machineName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Loại bảo trì <span class="text-danger">*</span></label>
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
                                <label class="form-label">Ngày thực hiện <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" name="maintenanceDate" id="createDate" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Người thực hiện</label>
                                <input type="text" class="form-control" name="performedBy" placeholder="Tên người thực hiện">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status">
                                    <option value="COMPLETED" selected>Hoàn thành</option>
                                    <option value="PENDING">Đang chờ</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mô tả</label>
                            <textarea class="form-control" name="description" rows="2" placeholder="Ghi chú..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Lưu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- EDIT MODAL -->
    <div class="modal fade" id="editModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title"><i class="fas fa-edit"></i> Sửa bảo trì</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" id="editId">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Máy <span class="text-danger">*</span></label>
                                <select class="form-select" name="machineId" id="editMachineId" required>
                                    <c:forEach var="machine" items="${machines}">
                                        <option value="${machine.id}">${machine.machineCode} - ${machine.machineName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Loại bảo trì <span class="text-danger">*</span></label>
                                <select class="form-select" name="maintenanceType" id="editType" required>
                                    <c:forEach var="type" items="${maintenanceTypes}">
                                        <option value="${type}">${type}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Ngày thực hiện <span class="text-danger">*</span></label>
                                <input type="date" class="form-control" name="maintenanceDate" id="editDate" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Người thực hiện</label>
                                <input type="text" class="form-control" name="performedBy" id="editPerformedBy">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Trạng thái</label>
                                <select class="form-select" name="status" id="editStatus">
                                    <option value="COMPLETED">Hoàn thành</option>
                                    <option value="PENDING">Đang chờ</option>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Mô tả</label>
                            <textarea class="form-control" name="description" id="editDescription" rows="2"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-warning"><i class="fas fa-save"></i> Lưu</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- DELETE MODAL -->
    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content modal-card">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title"><i class="fas fa-trash"></i> Xác nhận xóa</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/maintenances" method="POST">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" id="deleteId">
                    <div class="modal-body text-center">
                        <i class="fas fa-exclamation-triangle fa-3x text-danger mb-3"></i>
                        <p>Bạn có chắc chắn muốn xóa bản ghi này?</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-danger"><i class="fas fa-trash"></i> Xóa</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const ctx = '${pageContext.request.contextPath}';
            
            // Set default date
            document.getElementById('createDate').value = new Date().toISOString().split('T')[0];
            
            // Edit button
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
                                document.getElementById('editDate').value = m.maintenanceDate;
                                document.getElementById('editPerformedBy').value = m.performedBy || '';
                                document.getElementById('editStatus').value = m.status;
                                document.getElementById('editDescription').value = m.description || '';
                                new bootstrap.Modal(document.getElementById('editModal')).show();
                            }
                        });
                });
            });
            
            // Delete button
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

