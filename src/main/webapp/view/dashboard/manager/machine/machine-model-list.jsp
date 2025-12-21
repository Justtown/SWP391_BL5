<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Dòng máy - Argo Machine Management</title>
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

        .specs-display {
            background: #f8f9fa;
            border-radius: 5px;
            padding: 10px;
            font-family: monospace;
            font-size: 0.85rem;
            white-space: pre-wrap;
            max-height: 150px;
            overflow-y: auto;
        }

        .asset-count-badge {
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
            <h4 class="mb-1"><i class="fas fa-cubes me-2"></i>Quản lý Dòng máy</h4>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                    <li class="breadcrumb-item active">Dòng máy</li>
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
                <h5 class="mb-0">Danh sách dòng máy</h5>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">
                    <i class="fas fa-plus"></i> Thêm dòng máy
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
            <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/manager/machine-models">
                <div class="filter-section">
                    <select class="form-select filter-dropdown" id="typeIdFilter" name="typeId">
                        <option value="All">Tất cả loại</option>
                        <c:forEach var="type" items="${machineTypes}">
                            <option value="${type.id}" ${typeIdFilter == type.id ? 'selected' : ''}>${type.typeName}</option>
                        </c:forEach>
                    </select>

                    <select class="form-select filter-dropdown" id="brandFilter" name="brand">
                        <option value="All" ${brandFilter == 'All' ? 'selected' : ''}>Tất cả hãng</option>
                        <c:forEach var="b" items="${brands}">
                            <option value="${b}" ${brandFilter == b ? 'selected' : ''}>${b}</option>
                        </c:forEach>
                    </select>

                    <div class="search-container">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" class="form-control search-input" id="keyword" name="keyword"
                               placeholder="Tìm theo mã, tên, hãng..." value="${keyword}">
                    </div>

                    <button type="submit" class="btn btn-outline-primary">
                        <i class="fas fa-filter"></i> Lọc
                    </button>
                </div>
            </form>

            <!-- Pagination Info -->
            <c:if test="${not empty totalModels}">
                <div class="text-muted mb-3">
                    Hiển thị ${startIndex + 1} đến ${startIndex + models.size()} trong tổng số ${totalModels} dòng máy
                </div>
            </c:if>

            <!-- Model Table -->
            <div class="table-responsive">
                <table class="table table-bordered table-hover">
                    <thead class="table-light">
                    <tr>
                        <th>#</th>
                        <th>Mã dòng</th>
                        <th>Tên dòng máy</th>
                        <th>Thương hiệu</th>
                        <th>Loại máy</th>
                        <th>Số máy</th>
                        <th>Thao tác</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty models && models.size() > 0}">
                            <c:forEach var="model" items="${models}" varStatus="loop">
                                <tr>
                                    <td>${startIndex + loop.index + 1}</td>
                                    <td><strong>${model.modelCode}</strong></td>
                                    <td>${model.modelName}</td>
                                    <td><span class="badge bg-secondary">${model.brand}</span></td>
                                    <td>${model.typeName}</td>
                                    <td>
                                                <span class="badge bg-info asset-count-badge" id="assetCount_${model.id}">
                                                    <i class="fas fa-cogs"></i> --
                                                </span>
                                    </td>
                                    <td>
                                        <button type="button" class="btn btn-sm btn-info btn-detail"
                                                data-id="${model.id}" title="Xem chi tiết">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-warning btn-edit"
                                                data-id="${model.id}" title="Chỉnh sửa">
                                            <i class="fas fa-edit"></i>
                                        </button>
                                        <button type="button" class="btn btn-sm btn-danger btn-delete"
                                                data-id="${model.id}"
                                                data-name="${model.modelName}" title="Xóa">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="text-center text-muted py-4">
                                    <i class="fas fa-inbox fa-2x mb-2"></i><br>
                                    Không tìm thấy dòng máy nào
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
                    <i class="fas fa-plus-circle"></i> Thêm dòng máy mới
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/machine-models" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="createModelCode" class="form-label">Mã dòng máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                <input type="text" class="form-control" id="createModelCode" name="modelCode" required placeholder="VD: TR-KUB-L3408">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="createModelName" class="form-label">Tên dòng máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                <input type="text" class="form-control" id="createModelName" name="modelName" required placeholder="VD: Kubota L3408">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="createBrand" class="form-label">Thương hiệu <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-building"></i></span>
                                <input type="text" class="form-control" id="createBrand" name="brand" required placeholder="VD: Kubota, John Deere">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="createTypeId" class="form-label">Loại máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                <select class="form-select" id="createTypeId" name="typeId" required>
                                    <option value="">-- Chọn loại --</option>
                                    <c:forEach var="type" items="${machineTypes}">
                                        <option value="${type.id}">${type.typeName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="createSpecs" class="form-label">Thông số kỹ thuật (JSON)</label>
                        <textarea class="form-control" id="createSpecs" name="specs" rows="4"
                                  placeholder='{"power": "34HP", "weight": "1450kg", "fuel": "Diesel"}'></textarea>
                        <small class="text-muted">Nhập dưới dạng JSON hoặc để trống</small>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Hủy
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Tạo dòng máy
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
                    <i class="fas fa-info-circle"></i> Chi tiết dòng máy
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Mã dòng máy</label>
                        <p id="detailModelCode" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Tên dòng máy</label>
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
                        <label class="form-label fw-bold">Số máy liên kết</label>
                        <p id="detailAssetCount" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Ngày tạo</label>
                        <p id="detailCreatedAt" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold">Thông số kỹ thuật</label>
                    <div id="detailSpecs" class="specs-display">-</div>
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
                    <i class="fas fa-edit"></i> Chỉnh sửa dòng máy
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/machine-models" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" id="editId">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editModelCode" class="form-label">Mã dòng máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                <input type="text" class="form-control" id="editModelCode" name="modelCode" required>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editModelName" class="form-label">Tên dòng máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                <input type="text" class="form-control" id="editModelName" name="modelName" required>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editBrand" class="form-label">Thương hiệu <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-building"></i></span>
                                <input type="text" class="form-control" id="editBrand" name="brand" required>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editTypeId" class="form-label">Loại máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                <select class="form-select" id="editTypeId" name="typeId" required>
                                    <option value="">-- Chọn loại --</option>
                                    <c:forEach var="type" items="${machineTypes}">
                                        <option value="${type.id}">${type.typeName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editSpecs" class="form-label">Thông số kỹ thuật (JSON)</label>
                        <textarea class="form-control" id="editSpecs" name="specs" rows="4"></textarea>
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

<!-- DELETE MODAL -->
<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content modal-card">
            <div class="modal-header bg-danger text-white">
                <h5 class="modal-title" id="deleteModalLabel">
                    <i class="fas fa-exclamation-triangle"></i> Xác nhận xóa
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/manager/machine-models" method="POST">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="id" id="deleteId">
                <div class="modal-body">
                    <div class="text-center mb-3">
                        <i class="fas fa-trash fa-3x text-danger"></i>
                    </div>
                    <p class="text-center">
                        Bạn có chắc chắn muốn xóa dòng máy:<br>
                        <strong id="deleteModelName">-</strong>?
                    </p>
                    <div class="alert alert-warning">
                        <i class="fas fa-info-circle"></i>
                        Không thể xóa nếu còn máy đang liên kết với dòng máy này.
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Hủy
                    </button>
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-trash"></i> Xóa
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

        // Load asset counts for each model
        document.querySelectorAll('[id^="assetCount_"]').forEach(function(el) {
            const modelId = el.id.replace('assetCount_', '');
            loadAssetCount(modelId, el);
        });

        function loadAssetCount(modelId, element) {
            fetch(contextPath + '/manager/machine-models?action=detail&id=' + modelId)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        element.innerHTML = '<i class="fas fa-cogs"></i> ' + data.assetCount;
                    }
                })
                .catch(error => {
                    console.error('Error loading asset count:', error);
                });
        }

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

            window.location.href = contextPath + '/manager/machine-models?' + params.toString();
        }

        // Detail button click handlers
        document.querySelectorAll('.btn-detail').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                loadModelDetail(id);
            });
        });

        function loadModelDetail(id) {
            fetch(contextPath + '/manager/machine-models?action=detail&id=' + id)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        const m = data.model;
                        document.getElementById('detailModelCode').textContent = m.modelCode || '-';
                        document.getElementById('detailModelName').textContent = m.modelName || '-';
                        document.getElementById('detailBrand').textContent = m.brand || '-';
                        document.getElementById('detailTypeName').textContent = m.typeName || '-';
                        document.getElementById('detailAssetCount').textContent = data.assetCount + ' máy';
                        document.getElementById('detailCreatedAt').textContent = m.createdAt || '-';

                        // Format specs - hiển thị dạng danh sách
                        if (m.specs) {
                            try {
                                const specsObj = JSON.parse(m.specs);
                                let specsHtml = '<table class="table table-sm table-borderless mb-0">';
                                for (const [key, value] of Object.entries(specsObj)) {
                                    specsHtml += '<tr><td class="fw-bold text-muted" style="width:40%">' + key + '</td><td>' + value + '</td></tr>';
                                }
                                specsHtml += '</table>';
                                document.getElementById('detailSpecs').innerHTML = specsHtml;
                            } catch (e) {
                                document.getElementById('detailSpecs').textContent = m.specs;
                            }
                        } else {
                            document.getElementById('detailSpecs').innerHTML = '<span class="text-muted">Không có thông số</span>';
                        }

                        new bootstrap.Modal(document.getElementById('detailModal')).show();
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Không thể tải chi tiết dòng máy');
                });
        }

        // Edit button click handlers
        document.querySelectorAll('.btn-edit').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                loadModelForEdit(id);
            });
        });

        function loadModelForEdit(id) {
            fetch(contextPath + '/manager/machine-models?action=detail&id=' + id)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        const m = data.model;
                        document.getElementById('editId').value = m.id;
                        document.getElementById('editModelCode').value = m.modelCode || '';
                        document.getElementById('editModelName').value = m.modelName || '';
                        document.getElementById('editBrand').value = m.brand || '';
                        document.getElementById('editTypeId').value = m.typeId || '';
                        document.getElementById('editSpecs').value = m.specs || '';

                        new bootstrap.Modal(document.getElementById('editModal')).show();
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Không thể tải dòng máy để chỉnh sửa');
                });
        }

        // Delete button click handlers
        document.querySelectorAll('.btn-delete').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                const name = this.getAttribute('data-name');
                document.getElementById('deleteId').value = id;
                document.getElementById('deleteModelName').textContent = name;
                new bootstrap.Modal(document.getElementById('deleteModal')).show();
            });
        });
    });
</script>
</body>
</html>
