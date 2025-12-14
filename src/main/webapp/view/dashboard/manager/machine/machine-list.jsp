<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Machine Management - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: #f8f9fa;
        }
        
        .page-header {
            background: white;
            border-bottom: 1px solid #dee2e6;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .content-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .filter-section {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            align-items: center;
        }
        
        .filter-dropdown {
            width: 150px;
            flex-shrink: 0;
        }
        
        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 200px;
        }
        
        .search-input {
            padding-left: 40px;
        }
        
        .search-icon {
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }
        
        .pagination-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 20px;
            gap: 10px;
        }
        
        .pagination-btn {
            padding: 8px 16px;
            border: 1px solid #dee2e6;
            background: white;
            color: #495057;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
        }
        
        .pagination-btn:hover:not(.disabled) {
            background-color: #e9ecef;
            border-color: #adb5bd;
        }
        
        .pagination-btn.disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        
        .pagination-btn.active {
            background-color: #0d6efd;
            color: white;
            border-color: #0d6efd;
        }
        
        .modal-card {
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }
        
        .role-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
        }
        
        .role-manager { background-color: #0d6efd; color: white; }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Page Header -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-cogs me-2"></i>Machine Management</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Machines</li>
                    </ol>
                </nav>
            </div>
            <div class="d-flex align-items-center">
                <span class="me-3">
                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
                </span>
                <a href="#" class="btn btn-outline-danger btn-sm" data-bs-toggle="modal" data-bs-target="#logoutModal">
                    <i class="fas fa-sign-out-alt"></i> Đăng xuất
                </a>
            </div>
        </div>
        
        <!-- Content -->
        <div class="container-fluid">
            <div class="content-card">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h5 class="mb-0">Danh sách máy móc</h5>
                    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">
                        <i class="fas fa-plus"></i> Thêm máy mới
                    </button>
                </div>

                <!-- Alert Messages -->
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

                <!-- Filter Section -->
                <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/manager/machines">
                    <div class="filter-section">
                        <select class="form-select filter-dropdown" id="typeIdFilter" name="typeId">
                            <option value="All Types">Tất cả loại</option>
                            <c:forEach var="type" items="${machineTypes}">
                                <option value="${type.id}" ${typeIdFilter == type.id ? 'selected' : ''}>${type.typeName}</option>
                            </c:forEach>
                        </select>

                        <select class="form-select filter-dropdown" id="statusFilter" name="status">
                            <option value="All Status" ${statusFilter == 'All Status' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                            <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>Không hoạt động</option>
                            <option value="DISCONTINUED" ${statusFilter == 'DISCONTINUED' ? 'selected' : ''}>Ngừng sử dụng</option>
                        </select>

                        <div class="search-container">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" class="form-control search-input" id="keyword" name="keyword" 
                                   placeholder="Tìm theo mã, tên, vị trí..." value="${keyword}">
                        </div>

                        <button type="submit" class="btn btn-outline-primary">
                            <i class="fas fa-filter"></i> Lọc
                        </button>
                    </div>
                </form>

                <!-- Pagination Info -->
                <c:if test="${not empty totalMachines}">
                    <div class="text-muted mb-3">
                        Hiển thị ${startIndex + 1} đến ${startIndex + machines.size()} trong tổng số ${totalMachines} máy
                    </div>
                </c:if>

                <!-- Machine Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Mã máy</th>
                                <th>Tên máy</th>
                                <th>Loại</th>
                                <th>Trạng thái</th>
                                <th>Cho thuê</th>
                                <th>Vị trí</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty machines && machines.size() > 0}">
                                    <c:forEach var="machine" items="${machines}" varStatus="loop">
                                        <tr>
                                            <td>${startIndex + loop.index + 1}</td>
                                            <td><strong>${machine.machineCode}</strong></td>
                                            <td>${machine.machineName}</td>
                                            <td>${machine.machineTypeName}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${machine.status == 'ACTIVE'}">
                                                        <span class="badge bg-success">Hoạt động</span>
                                                    </c:when>
                                                    <c:when test="${machine.status == 'INACTIVE'}">
                                                        <span class="badge bg-danger">Không hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-secondary">Ngừng sử dụng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${machine.isRentable}">
                                                        <span class="text-success"><i class="fas fa-check"></i> Có</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-muted"><i class="fas fa-times"></i> Không</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${machine.location != null ? machine.location : '-'}</td>
                                            <td>
                                                <button type="button" class="btn btn-sm btn-info btn-detail" 
                                                        data-id="${machine.id}" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button type="button" class="btn btn-sm btn-warning btn-edit" 
                                                        data-id="${machine.id}" title="Chỉnh sửa">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <c:if test="${machine.status == 'ACTIVE'}">
                                                    <button type="button" class="btn btn-sm btn-danger btn-deactivate" 
                                                            data-id="${machine.id}" 
                                                            data-name="${machine.machineName}" title="Vô hiệu hóa">
                                                        <i class="fas fa-power-off"></i>
                                                    </button>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center text-muted py-4">
                                            <i class="fas fa-inbox fa-2x mb-2"></i><br>
                                            Không tìm thấy máy nào
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${not empty totalPages && totalPages > 1}">
                    <div class="pagination-container">
                        <c:choose>
                            <c:when test="${currentPage > 1}">
                                <a href="#" class="pagination-btn" data-page="${currentPage - 1}">
                                    <i class="fas fa-chevron-left"></i> Trước
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="pagination-btn disabled">
                                    <i class="fas fa-chevron-left"></i> Trước
                                </span>
                            </c:otherwise>
                        </c:choose>

                        <c:forEach var="i" begin="1" end="${totalPages}">
                            <c:choose>
                                <c:when test="${i == currentPage}">
                                    <span class="pagination-btn active">${i}</span>
                                </c:when>
                                <c:when test="${i == 1 || i == totalPages || (i >= currentPage - 1 && i <= currentPage + 1)}">
                                    <a href="#" class="pagination-btn" data-page="${i}">${i}</a>
                                </c:when>
                                <c:when test="${i == currentPage - 2 || i == currentPage + 2}">
                                    <span class="pagination-btn disabled">...</span>
                                </c:when>
                            </c:choose>
                        </c:forEach>

                        <c:choose>
                            <c:when test="${currentPage < totalPages}">
                                <a href="#" class="pagination-btn" data-page="${currentPage + 1}">
                                    Sau <i class="fas fa-chevron-right"></i>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="pagination-btn disabled">
                                    Sau <i class="fas fa-chevron-right"></i>
                                </span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

    <!-- ==================== MODALS ==================== -->

    <!-- CREATE MODAL -->
    <div class="modal fade" id="createModal" tabindex="-1" aria-labelledby="createModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title" id="createModalLabel">
                        <i class="fas fa-plus-circle"></i> Thêm máy mới
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machines" method="POST">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createMachineCode" class="form-label">Mã máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                    <input type="text" class="form-control" id="createMachineCode" name="machineCode" required placeholder="VD: TR-001">
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="createMachineName" class="form-label">Tên máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                    <input type="text" class="form-control" id="createMachineName" name="machineName" required placeholder="VD: Máy kéo Kubota L3408">
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createMachineTypeId" class="form-label">Loại máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                    <select class="form-select" id="createMachineTypeId" name="machineTypeId" required>
                                        <option value="">-- Chọn loại --</option>
                                        <c:forEach var="type" items="${machineTypes}">
                                            <option value="${type.id}">${type.typeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="createStatus" class="form-label">Trạng thái</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                    <select class="form-select" id="createStatus" name="status">
                                        <option value="ACTIVE" selected>Hoạt động</option>
                                        <option value="INACTIVE">Không hoạt động</option>
                                        <option value="DISCONTINUED">Ngừng sử dụng</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createLocation" class="form-label">Vị trí</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                    <input type="text" class="form-control" id="createLocation" name="location" placeholder="VD: Trang trại A">
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="createPurchaseDate" class="form-label">Ngày mua</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-calendar-alt"></i></span>
                                    <input type="date" class="form-control" id="createPurchaseDate" name="purchaseDate">
                                </div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="createIsRentable" name="isRentable" checked>
                                <label class="form-check-label" for="createIsRentable">
                                    <i class="fas fa-hand-holding"></i> Có thể cho thuê
                                </label>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="createDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="createDescription" name="description" rows="3" placeholder="Nhập mô tả máy..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Tạo máy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- DETAIL MODAL -->
    <div class="modal fade" id="detailModal" tabindex="-1" aria-labelledby="detailModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title" id="detailModalLabel">
                        <i class="fas fa-info-circle"></i> Chi tiết máy
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Mã máy</label>
                            <p id="detailMachineCode" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Tên máy</label>
                            <p id="detailMachineName" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Loại máy</label>
                            <p id="detailMachineType" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Trạng thái</label>
                            <p id="detailStatus" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Vị trí</label>
                            <p id="detailLocation" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Ngày mua</label>
                            <p id="detailPurchaseDate" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Có thể thuê</label>
                            <p id="detailRentable" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Ngày tạo</label>
                            <p id="detailCreatedAt" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Mô tả</label>
                        <p id="detailDescription" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Đóng
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- EDIT MODAL -->
    <div class="modal fade" id="editModal" tabindex="-1" aria-labelledby="editModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title" id="editModalLabel">
                        <i class="fas fa-edit"></i> Chỉnh sửa máy
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machines" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" id="editId">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editMachineCode" class="form-label">Mã máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                    <input type="text" class="form-control" id="editMachineCode" name="machineCode" required>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editMachineName" class="form-label">Tên máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                    <input type="text" class="form-control" id="editMachineName" name="machineName" required>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editMachineTypeId" class="form-label">Loại máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                    <select class="form-select" id="editMachineTypeId" name="machineTypeId" required>
                                        <option value="">-- Chọn loại --</option>
                                        <c:forEach var="type" items="${machineTypes}">
                                            <option value="${type.id}">${type.typeName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editStatus" class="form-label">Trạng thái</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                    <select class="form-select" id="editStatus" name="status">
                                        <option value="ACTIVE">Hoạt động</option>
                                        <option value="INACTIVE">Không hoạt động</option>
                                        <option value="DISCONTINUED">Ngừng sử dụng</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editLocation" class="form-label">Vị trí</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                    <input type="text" class="form-control" id="editLocation" name="location">
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editPurchaseDate" class="form-label">Ngày mua</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-calendar-alt"></i></span>
                                    <input type="date" class="form-control" id="editPurchaseDate" name="purchaseDate">
                                </div>
                            </div>
                        </div>
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="editIsRentable" name="isRentable">
                                <label class="form-check-label" for="editIsRentable">
                                    <i class="fas fa-hand-holding"></i> Có thể cho thuê
                                </label>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-warning">
                            <i class="fas fa-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- DEACTIVATE MODAL -->
    <div class="modal fade" id="deactivateModal" tabindex="-1" aria-labelledby="deactivateModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content modal-card">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title" id="deactivateModalLabel">
                        <i class="fas fa-exclamation-triangle"></i> Xác nhận vô hiệu hóa
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machines" method="POST">
                    <input type="hidden" name="action" value="deactivate">
                    <input type="hidden" name="id" id="deactivateId">
                    <div class="modal-body">
                        <div class="text-center mb-3">
                            <i class="fas fa-power-off fa-3x text-danger"></i>
                        </div>
                        <p class="text-center">
                            Bạn có chắc chắn muốn vô hiệu hóa máy:<br>
                            <strong id="deactivateMachineName">-</strong>?
                        </p>
                        <div class="alert alert-warning">
                            <i class="fas fa-info-circle"></i>
                            Máy này sẽ được đặt thành <strong>KHÔNG HOẠT ĐỘNG</strong> và không thể cho thuê.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-power-off"></i> Vô hiệu hóa
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- LOGOUT MODAL -->
    <div class="modal fade" id="logoutModal" tabindex="-1" aria-labelledby="logoutModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content" style="border-radius: 10px; box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2); border: none;">
                <div class="modal-body p-5 text-center">
                    <h2 class="mb-4">Đăng xuất</h2>
                    <p class="text-muted mb-4">Bạn có muốn đăng xuất không?</p>
                    <div class="d-flex justify-content-center gap-3">
                        <form action="${pageContext.request.contextPath}/logout" method="POST" style="display: inline;">
                            <input type="hidden" name="confirm" value="yes">
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="fas fa-check"></i> Có
                            </button>
                        </form>
                        <button type="button" class="btn btn-secondary px-4" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Không
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const contextPath = '${pageContext.request.contextPath}';

            // Pagination click handlers
            document.querySelectorAll('.pagination-btn[data-page]').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const page = this.getAttribute('data-page');
                    goToPage(parseInt(page));
                });
            });

            function goToPage(page) {
                const form = document.getElementById('filterForm');
                const formData = new FormData(form);
                const params = new URLSearchParams();
                
                for (let [key, value] of formData.entries()) {
                    if (value && value !== 'All Types' && value !== 'All Status') {
                        params.append(key, value);
                    }
                }
                params.set('page', page);
                
                window.location.href = contextPath + '/manager/machines?' + params.toString();
            }

            // Detail button click handlers
            document.querySelectorAll('.btn-detail').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    loadMachineDetail(id);
                });
            });

            function loadMachineDetail(id) {
                fetch(contextPath + '/manager/machines?action=detail&id=' + id)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            const m = data.machine;
                            document.getElementById('detailMachineCode').textContent = m.machineCode || '-';
                            document.getElementById('detailMachineName').textContent = m.machineName || '-';
                            document.getElementById('detailMachineType').textContent = m.machineTypeName || '-';
                            document.getElementById('detailStatus').innerHTML = getStatusBadge(m.status);
                            document.getElementById('detailLocation').textContent = m.location || '-';
                            document.getElementById('detailPurchaseDate').textContent = m.purchaseDate || '-';
                            document.getElementById('detailRentable').innerHTML = m.isRentable ? 
                                '<span class="text-success"><i class="fas fa-check"></i> Có</span>' : 
                                '<span class="text-muted"><i class="fas fa-times"></i> Không</span>';
                            document.getElementById('detailCreatedAt').textContent = m.createdAt || '-';
                            document.getElementById('detailDescription').textContent = m.description || '-';
                            
                            new bootstrap.Modal(document.getElementById('detailModal')).show();
                        } else {
                            alert('Lỗi: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Không thể tải chi tiết máy');
                    });
            }

            function getStatusBadge(status) {
                if (status === 'ACTIVE') return '<span class="badge bg-success">Hoạt động</span>';
                if (status === 'INACTIVE') return '<span class="badge bg-danger">Không hoạt động</span>';
                return '<span class="badge bg-secondary">Ngừng sử dụng</span>';
            }

            // Edit button click handlers
            document.querySelectorAll('.btn-edit').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    loadMachineForEdit(id);
                });
            });

            function loadMachineForEdit(id) {
                fetch(contextPath + '/manager/machines?action=detail&id=' + id)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            const m = data.machine;
                            document.getElementById('editId').value = m.id;
                            document.getElementById('editMachineCode').value = m.machineCode || '';
                            document.getElementById('editMachineName').value = m.machineName || '';
                            document.getElementById('editMachineTypeId').value = m.machineTypeId || '';
                            document.getElementById('editStatus').value = m.status || 'ACTIVE';
                            document.getElementById('editLocation').value = m.location || '';
                            document.getElementById('editPurchaseDate').value = m.purchaseDate || '';
                            document.getElementById('editIsRentable').checked = m.isRentable;
                            document.getElementById('editDescription').value = m.description || '';
                            
                            new bootstrap.Modal(document.getElementById('editModal')).show();
                        } else {
                            alert('Lỗi: ' + data.message);
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Không thể tải máy để chỉnh sửa');
                    });
            }

            // Deactivate button click handlers
            document.querySelectorAll('.btn-deactivate').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    const name = this.getAttribute('data-name');
                    document.getElementById('deactivateId').value = id;
                    document.getElementById('deactivateMachineName').textContent = name;
                    new bootstrap.Modal(document.getElementById('deactivateModal')).show();
                });
            });
        });
    </script>
</body>
</html>
