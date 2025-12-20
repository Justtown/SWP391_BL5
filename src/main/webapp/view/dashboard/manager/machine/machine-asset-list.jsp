<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Máy - Argo Machine Management</title>
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

        .status-badge {
            font-size: 0.8rem;
        }

        .rental-badge {
            font-size: 0.75rem;
        }

        .action-btn-group .btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.75rem;
        }
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
                <h4 class="mb-1"><i class="fas fa-cogs me-2"></i>Quản lý Máy</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Danh sách máy</li>
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
                    <h5 class="mb-0">Danh sách máy</h5>
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
                <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/manager/machine-assets">
                    <div class="filter-section">
                        <select class="form-select filter-dropdown" id="modelIdFilter" name="modelId">
                            <option value="All">Tất cả dòng máy</option>
                            <c:forEach var="model" items="${machineModels}">
                                <option value="${model.id}" ${modelIdFilter == model.id ? 'selected' : ''}>${model.modelName}</option>
                            </c:forEach>
                        </select>

                        <select class="form-select filter-dropdown" id="statusFilter" name="status">
                            <option value="All" ${statusFilter == 'All' ? 'selected' : ''}>Tất cả trạng thái</option>
                            <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Hoạt động</option>
                            <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>Không hoạt động</option>
                        </select>

                        <select class="form-select filter-dropdown" id="rentalStatusFilter" name="rentalStatus">
                            <option value="All" ${rentalStatusFilter == 'All' ? 'selected' : ''}>Tất cả thuê</option>
                            <option value="AVAILABLE" ${rentalStatusFilter == 'AVAILABLE' ? 'selected' : ''}>Sẵn sàng</option>
                            <option value="RENTED" ${rentalStatusFilter == 'RENTED' ? 'selected' : ''}>Đang thuê</option>
                            <option value="MAINTENANCE" ${rentalStatusFilter == 'MAINTENANCE' ? 'selected' : ''}>Bảo trì</option>
                        </select>

                        <div class="search-container">
                            <i class="fas fa-search search-icon"></i>
                            <input type="text" class="form-control search-input" id="keyword" name="keyword"
                                   placeholder="Tìm theo serial, vị trí..." value="${keyword}">
                        </div>

                        <button type="submit" class="btn btn-outline-primary">
                            <i class="fas fa-filter"></i> Lọc
                        </button>
                    </div>
                </form>

                <!-- Pagination Info -->
                <c:if test="${not empty totalAssets}">
                    <div class="text-muted mb-3">
                        Hiển thị ${startIndex + 1} đến ${startIndex + assets.size()} trong tổng số ${totalAssets} máy
                    </div>
                </c:if>

                <!-- Asset Table -->
                <div class="table-responsive">
                    <table class="table table-bordered table-hover">
                        <thead class="table-light">
                            <tr>
                                <th>#</th>
                                <th>Serial Number</th>
                                <th>Dòng máy</th>
                                <th>Thương hiệu</th>
                                <th>Trạng thái</th>
                                <th>Thuê</th>
                                <th>Vị trí</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty assets && assets.size() > 0}">
                                    <c:forEach var="asset" items="${assets}" varStatus="loop">
                                        <tr>
                                            <td>${startIndex + loop.index + 1}</td>
                                            <td><strong>${asset.serialNumber}</strong></td>
                                            <td>${asset.modelName}</td>
                                            <td><span class="badge bg-secondary">${asset.brand}</span></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${asset.status == 'ACTIVE'}">
                                                        <span class="badge bg-success status-badge">Hoạt động</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger status-badge">Không hoạt động</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${asset.rentalStatus == 'AVAILABLE'}">
                                                        <span class="badge bg-success rental-badge"><i class="fas fa-check"></i> Sẵn sàng</span>
                                                    </c:when>
                                                    <c:when test="${asset.rentalStatus == 'RENTED'}">
                                                        <span class="badge bg-primary rental-badge"><i class="fas fa-handshake"></i> Đang thuê</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning text-dark rental-badge"><i class="fas fa-wrench"></i> Bảo trì</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>${asset.location != null ? asset.location : '-'}</td>
                                            <td class="action-btn-group">
                                                <button type="button" class="btn btn-sm btn-info btn-detail"
                                                        data-id="${asset.id}" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </button>
                                                <button type="button" class="btn btn-sm btn-warning btn-edit"
                                                        data-id="${asset.id}" title="Chỉnh sửa">
                                                    <i class="fas fa-edit"></i>
                                                </button>
                                                <c:if test="${asset.status == 'ACTIVE' && asset.rentalStatus != 'RENTED'}">
                                                    <button type="button" class="btn btn-sm btn-danger btn-deactivate"
                                                            data-id="${asset.id}"
                                                            data-serial="${asset.serialNumber}" title="Vô hiệu hóa">
                                                        <i class="fas fa-power-off"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${asset.status == 'INACTIVE'}">
                                                    <button type="button" class="btn btn-sm btn-success btn-activate"
                                                            data-id="${asset.id}" title="Kích hoạt">
                                                        <i class="fas fa-play"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${asset.status == 'ACTIVE' && asset.rentalStatus == 'AVAILABLE'}">
                                                    <button type="button" class="btn btn-sm btn-secondary btn-maintenance"
                                                            data-id="${asset.id}" title="Bảo trì">
                                                        <i class="fas fa-wrench"></i>
                                                    </button>
                                                </c:if>
                                                <c:if test="${asset.status == 'ACTIVE' && asset.rentalStatus == 'MAINTENANCE'}">
                                                    <button type="button" class="btn btn-sm btn-success btn-available"
                                                            data-id="${asset.id}" title="Sẵn sàng">
                                                        <i class="fas fa-check"></i>
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
                <form action="${pageContext.request.contextPath}/manager/machine-assets" method="POST">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createSerialNumber" class="form-label">Serial Number <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                    <input type="text" class="form-control" id="createSerialNumber" name="serialNumber" required placeholder="VD: KUB-L3408-001">
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="createModelId" class="form-label">Dòng máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-cubes"></i></span>
                                    <select class="form-select" id="createModelId" name="modelId" required>
                                        <option value="">-- Chọn dòng máy --</option>
                                        <c:forEach var="model" items="${machineModels}">
                                            <option value="${model.id}">${model.modelCode} - ${model.modelName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createStatus" class="form-label">Trạng thái</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                    <select class="form-select" id="createStatus" name="status">
                                        <option value="ACTIVE" selected>Hoạt động</option>
                                        <option value="INACTIVE">Không hoạt động</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="createRentalStatus" class="form-label">Trạng thái thuê</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-handshake"></i></span>
                                    <select class="form-select" id="createRentalStatus" name="rentalStatus">
                                        <option value="AVAILABLE" selected>Sẵn sàng</option>
                                        <option value="MAINTENANCE">Bảo trì</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="createLocation" class="form-label">Vị trí</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                    <input type="text" class="form-control" id="createLocation" name="location" placeholder="VD: Kho Hà Nội">
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
                            <label for="createNote" class="form-label">Ghi chú</label>
                            <textarea class="form-control" id="createNote" name="note" rows="3" placeholder="Nhập ghi chú..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Thêm máy
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
                            <label class="form-label fw-bold">Serial Number</label>
                            <p id="detailSerialNumber" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Dòng máy</label>
                            <p id="detailModelName" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Thương hiệu</label>
                            <p id="detailBrand" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Loại máy</label>
                            <p id="detailTypeName" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Trạng thái</label>
                            <p id="detailStatus" class="form-control-plaintext">-</p>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label fw-bold">Trạng thái thuê</label>
                            <p id="detailRentalStatus" class="form-control-plaintext">-</p>
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
                            <label class="form-label fw-bold">Ngày tạo</label>
                            <p id="detailCreatedAt" class="form-control-plaintext">-</p>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-bold">Ghi chú</label>
                        <p id="detailNote" class="form-control-plaintext">-</p>
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
                <form action="${pageContext.request.contextPath}/manager/machine-assets" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" id="editId">
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editSerialNumber" class="form-label">Serial Number <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                    <input type="text" class="form-control" id="editSerialNumber" name="serialNumber" required>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editModelId" class="form-label">Dòng máy <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-cubes"></i></span>
                                    <select class="form-select" id="editModelId" name="modelId" required>
                                        <option value="">-- Chọn dòng máy --</option>
                                        <c:forEach var="model" items="${machineModels}">
                                            <option value="${model.id}">${model.modelCode} - ${model.modelName}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label for="editStatus" class="form-label">Trạng thái</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                    <select class="form-select" id="editStatus" name="status">
                                        <option value="ACTIVE">Hoạt động</option>
                                        <option value="INACTIVE">Không hoạt động</option>
                                    </select>
                                </div>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label for="editRentalStatus" class="form-label">Trạng thái thuê</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="fas fa-handshake"></i></span>
                                    <select class="form-select" id="editRentalStatus" name="rentalStatus">
                                        <option value="AVAILABLE">Sẵn sàng</option>
                                        <option value="RENTED">Đang thuê</option>
                                        <option value="MAINTENANCE">Bảo trì</option>
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
                            <label for="editNote" class="form-label">Ghi chú</label>
                            <textarea class="form-control" id="editNote" name="note" rows="3"></textarea>
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
                <form action="${pageContext.request.contextPath}/manager/machine-assets" method="POST">
                    <input type="hidden" name="action" value="deactivate">
                    <input type="hidden" name="id" id="deactivateId">
                    <div class="modal-body">
                        <div class="text-center mb-3">
                            <i class="fas fa-power-off fa-3x text-danger"></i>
                        </div>
                        <p class="text-center">
                            Bạn có chắc chắn muốn vô hiệu hóa máy:<br>
                            <strong id="deactivateSerial">-</strong>?
                        </p>
                        <div class="alert alert-warning">
                            <i class="fas fa-info-circle"></i>
                            Máy này sẽ được đặt thành <strong>KHÔNG HOẠT ĐỘNG</strong>.
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
                    if (value && value !== 'All') {
                        params.append(key, value);
                    }
                }
                params.set('page', page);

                window.location.href = contextPath + '/manager/machine-assets?' + params.toString();
            }

            // Detail button click handlers
            document.querySelectorAll('.btn-detail').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    loadAssetDetail(id);
                });
            });

            function loadAssetDetail(id) {
                fetch(contextPath + '/manager/machine-assets?action=detail&id=' + id)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            const a = data.asset;
                            document.getElementById('detailSerialNumber').textContent = a.serialNumber || '-';
                            document.getElementById('detailModelName').textContent = a.modelName || '-';
                            document.getElementById('detailBrand').textContent = a.brand || '-';
                            document.getElementById('detailTypeName').textContent = a.typeName || '-';
                            document.getElementById('detailStatus').innerHTML = getStatusBadge(a.status);
                            document.getElementById('detailRentalStatus').innerHTML = getRentalStatusBadge(a.rentalStatus);
                            document.getElementById('detailLocation').textContent = a.location || '-';
                            document.getElementById('detailPurchaseDate').textContent = a.purchaseDate || '-';
                            document.getElementById('detailCreatedAt').textContent = a.createdAt || '-';
                            document.getElementById('detailNote').textContent = a.note || '-';

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
                return '<span class="badge bg-danger">Không hoạt động</span>';
            }

            function getRentalStatusBadge(rentalStatus) {
                if (rentalStatus === 'AVAILABLE') return '<span class="badge bg-success">Sẵn sàng</span>';
                if (rentalStatus === 'RENTED') return '<span class="badge bg-primary">Đang thuê</span>';
                return '<span class="badge bg-warning text-dark">Bảo trì</span>';
            }

            // Edit button click handlers
            document.querySelectorAll('.btn-edit').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    loadAssetForEdit(id);
                });
            });

            function loadAssetForEdit(id) {
                fetch(contextPath + '/manager/machine-assets?action=detail&id=' + id)
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            const a = data.asset;
                            document.getElementById('editId').value = a.id;
                            document.getElementById('editSerialNumber').value = a.serialNumber || '';
                            document.getElementById('editModelId').value = a.modelId || '';
                            document.getElementById('editStatus').value = a.status || 'ACTIVE';
                            document.getElementById('editRentalStatus').value = a.rentalStatus || 'AVAILABLE';
                            document.getElementById('editLocation').value = a.location || '';
                            document.getElementById('editPurchaseDate').value = a.purchaseDate || '';
                            document.getElementById('editNote').value = a.note || '';

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
                    const serial = this.getAttribute('data-serial');
                    document.getElementById('deactivateId').value = id;
                    document.getElementById('deactivateSerial').textContent = serial;
                    new bootstrap.Modal(document.getElementById('deactivateModal')).show();
                });
            });

            // Activate button handlers
            document.querySelectorAll('.btn-activate').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    if (confirm('Bạn có chắc muốn kích hoạt máy này?')) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = contextPath + '/manager/machine-assets';

                        const actionInput = document.createElement('input');
                        actionInput.type = 'hidden';
                        actionInput.name = 'action';
                        actionInput.value = 'activate';
                        form.appendChild(actionInput);

                        const idInput = document.createElement('input');
                        idInput.type = 'hidden';
                        idInput.name = 'id';
                        idInput.value = id;
                        form.appendChild(idInput);

                        document.body.appendChild(form);
                        form.submit();
                    }
                });
            });

            // Maintenance button handlers
            document.querySelectorAll('.btn-maintenance').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    if (confirm('Bạn có chắc muốn chuyển máy này sang bảo trì?')) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = contextPath + '/manager/machine-assets';

                        const actionInput = document.createElement('input');
                        actionInput.type = 'hidden';
                        actionInput.name = 'action';
                        actionInput.value = 'setMaintenance';
                        form.appendChild(actionInput);

                        const idInput = document.createElement('input');
                        idInput.type = 'hidden';
                        idInput.name = 'id';
                        idInput.value = id;
                        form.appendChild(idInput);

                        document.body.appendChild(form);
                        form.submit();
                    }
                });
            });

            // Available button handlers
            document.querySelectorAll('.btn-available').forEach(function(btn) {
                btn.addEventListener('click', function() {
                    const id = this.getAttribute('data-id');
                    if (confirm('Bạn có chắc muốn chuyển máy này sang sẵn sàng?')) {
                        const form = document.createElement('form');
                        form.method = 'POST';
                        form.action = contextPath + '/manager/machine-assets';

                        const actionInput = document.createElement('input');
                        actionInput.type = 'hidden';
                        actionInput.name = 'action';
                        actionInput.value = 'setAvailable';
                        form.appendChild(actionInput);

                        const idInput = document.createElement('input');
                        idInput.type = 'hidden';
                        idInput.name = 'id';
                        idInput.value = id;
                        form.appendChild(idInput);

                        document.body.appendChild(form);
                        form.submit();
                    }
                });
            });
        });
    </script>
</body>
</html>
