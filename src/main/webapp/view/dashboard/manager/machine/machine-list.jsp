<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Machine Management - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f5f7fa;
        }
        .logo {
            margin-top: 20px;
            text-align: center;
        }
        .title {
            font-size: 18px;
            color: #1e4e79;
            margin-top: 5px;
            text-align: center;
        }
        .menu {
            margin-top: 15px;
            background: #1e4e79;
            padding: 12px 0;
            text-align: center;
        }
        .menu a {
            color: white;
            text-decoration: none;
            margin: 0 25px;
            font-size: 15px;
            font-weight: bold;
        }
        .menu a:hover { opacity: 0.7; }
        .footer {
            background: #1e4e79;
            color: white;
            padding: 15px 0;
            text-align: center;
            margin-top: 30px;
        }
        .status-active { color: #28a745; font-weight: 500; }
        .status-inactive { color: #dc3545; font-weight: 500; }
        .status-discontinued { color: #6c757d; font-weight: 500; }
        .modal-card {
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
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
    </style>
</head>
<body>

<%-- Include Header --%>
<%@ include file="/view/common/dashboard/header.jsp" %>

<!-- Main Content -->
<div class="container-fluid py-4">
    <div class="row">
        <%-- Include Sidebar --%>
        <%@ include file="/view/common/dashboard/sideBar.jsp" %>
        
        <!-- Content Area -->
        <div class="col-lg-10 col-md-9">
            <div class="card">
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h4 class="card-title mb-0">
                            <i class="fas fa-cogs"></i> Machine Management
                        </h4>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createModal">
                            <i class="fas fa-plus"></i> Add New Machine
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
                    <form id="filterForm" method="GET" action="${pageContext.request.contextPath}/machines">
                        <div class="filter-section">
                            <select class="form-select filter-dropdown" id="typeIdFilter" name="typeId">
                                <option value="All Types">All Types</option>
                                <c:forEach var="type" items="${machineTypes}">
                                    <option value="${type.id}" ${typeIdFilter == type.id ? 'selected' : ''}>${type.typeName}</option>
                                </c:forEach>
                            </select>

                            <select class="form-select filter-dropdown" id="statusFilter" name="status">
                                <option value="All Status" ${statusFilter == 'All Status' ? 'selected' : ''}>All Status</option>
                                <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Active</option>
                                <option value="INACTIVE" ${statusFilter == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                                <option value="DISCONTINUED" ${statusFilter == 'DISCONTINUED' ? 'selected' : ''}>Discontinued</option>
                            </select>

                            <div class="search-container">
                                <i class="fas fa-search search-icon"></i>
                                <input type="text" class="form-control search-input" id="keyword" name="keyword" 
                                       placeholder="Search by code, name, location..." value="${keyword}">
                            </div>

                            <button type="submit" class="btn btn-outline-primary">
                                <i class="fas fa-filter"></i> Filter
                            </button>
                        </div>
                    </form>

                    <!-- Pagination Info -->
                    <c:if test="${not empty totalMachines}">
                        <div class="text-muted mb-3">
                            Showing ${startIndex + 1} to ${startIndex + machines.size()} of ${totalMachines} machines
                        </div>
                    </c:if>

                    <!-- Machine Table -->
                    <div class="table-responsive">
                        <table class="table table-bordered table-hover">
                            <thead class="table-light">
                                <tr>
                                    <th>#</th>
                                    <th>Code</th>
                                    <th>Name</th>
                                    <th>Type</th>
                                    <th>Status</th>
                                    <th>Rentable</th>
                                    <th>Location</th>
                                    <th>Actions</th>
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
                                                            <span class="badge bg-success">Active</span>
                                                        </c:when>
                                                        <c:when test="${machine.status == 'INACTIVE'}">
                                                            <span class="badge bg-danger">Inactive</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-secondary">Discontinued</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${machine.isRentable}">
                                                            <span class="text-success"><i class="fas fa-check"></i> Yes</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-muted"><i class="fas fa-times"></i> No</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${machine.location != null ? machine.location : '-'}</td>
                                                <td>
                                                    <button type="button" class="btn btn-sm btn-info btn-detail" 
                                                            data-id="${machine.id}" title="View Detail">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <button type="button" class="btn btn-sm btn-warning btn-edit" 
                                                            data-id="${machine.id}" title="Edit">
                                                        <i class="fas fa-edit"></i>
                                                    </button>
                                                    <c:if test="${machine.status == 'ACTIVE'}">
                                                        <button type="button" class="btn btn-sm btn-danger btn-deactivate" 
                                                                data-id="${machine.id}" 
                                                                data-name="${machine.machineName}" title="Deactivate">
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
                                                No machines found
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
                                        <i class="fas fa-chevron-left"></i> Previous
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span class="pagination-btn disabled">
                                        <i class="fas fa-chevron-left"></i> Previous
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
                                        Next <i class="fas fa-chevron-right"></i>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <span class="pagination-btn disabled">
                                        Next <i class="fas fa-chevron-right"></i>
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<%-- Include Footer --%>
<%@ include file="/view/common/dashboard/footer.jsp" %>

<!-- ==================== MODALS ==================== -->

<!-- CREATE MODAL -->
<div class="modal fade" id="createModal" tabindex="-1" aria-labelledby="createModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-lg">
        <div class="modal-content modal-card">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title" id="createModalLabel">
                    <i class="fas fa-plus-circle"></i> Add New Machine
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/machines" method="POST">
                <input type="hidden" name="action" value="create">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="createMachineCode" class="form-label">Machine Code <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                <input type="text" class="form-control" id="createMachineCode" name="machineCode" required placeholder="e.g., TR-001">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="createMachineName" class="form-label">Machine Name <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                <input type="text" class="form-control" id="createMachineName" name="machineName" required placeholder="e.g., Tractor Kubota L3408">
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="createMachineTypeId" class="form-label">Machine Type <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                <select class="form-select" id="createMachineTypeId" name="machineTypeId" required>
                                    <option value="">-- Select Type --</option>
                                    <c:forEach var="type" items="${machineTypes}">
                                        <option value="${type.id}">${type.typeName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="createStatus" class="form-label">Status</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                <select class="form-select" id="createStatus" name="status">
                                    <option value="ACTIVE" selected>Active</option>
                                    <option value="INACTIVE">Inactive</option>
                                    <option value="DISCONTINUED">Discontinued</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="createLocation" class="form-label">Location</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                <input type="text" class="form-control" id="createLocation" name="location" placeholder="e.g., Farm A">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="createPurchaseDate" class="form-label">Purchase Date</label>
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
                                 Available for Rent
                            </label>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="createDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="createDescription" name="description" rows="3" placeholder="Enter machine description..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-primary">
                        <i class="fas fa-save"></i> Create Machine
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
                    <i class="fas fa-info-circle"></i> Machine Details
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Machine Code</label>
                        <p id="detailMachineCode" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Machine Name</label>
                        <p id="detailMachineName" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Machine Type</label>
                        <p id="detailMachineType" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Status</label>
                        <p id="detailStatus" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Location</label>
                        <p id="detailLocation" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Purchase Date</label>
                        <p id="detailPurchaseDate" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Rentable</label>
                        <p id="detailRentable" class="form-control-plaintext">-</p>
                    </div>
                    <div class="col-md-6 mb-3">
                        <label class="form-label fw-bold">Created At</label>
                        <p id="detailCreatedAt" class="form-control-plaintext">-</p>
                    </div>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-bold">Description</label>
                    <p id="detailDescription" class="form-control-plaintext">-</p>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                    <i class="fas fa-times"></i> Close
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
                    <i class="fas fa-edit"></i> Edit Machine
                </h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/machines" method="POST">
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" id="editId">
                <div class="modal-body">
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editMachineCode" class="form-label">Machine Code <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-barcode"></i></span>
                                <input type="text" class="form-control" id="editMachineCode" name="machineCode" required>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editMachineName" class="form-label">Machine Name <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-cog"></i></span>
                                <input type="text" class="form-control" id="editMachineName" name="machineName" required>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editMachineTypeId" class="form-label">Machine Type <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-th-list"></i></span>
                                <select class="form-select" id="editMachineTypeId" name="machineTypeId" required>
                                    <option value="">-- Select Type --</option>
                                    <c:forEach var="type" items="${machineTypes}">
                                        <option value="${type.id}">${type.typeName}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editStatus" class="form-label">Status</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-toggle-on"></i></span>
                                <select class="form-select" id="editStatus" name="status">
                                    <option value="ACTIVE">Active</option>
                                    <option value="INACTIVE">Inactive</option>
                                    <option value="DISCONTINUED">Discontinued</option>
                                </select>
                            </div>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="editLocation" class="form-label">Location</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-map-marker-alt"></i></span>
                                <input type="text" class="form-control" id="editLocation" name="location">
                            </div>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="editPurchaseDate" class="form-label">Purchase Date</label>
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
                                 Available for Rent
                            </label>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label for="editDescription" class="form-label">Description</label>
                        <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-warning">
                        <i class="fas fa-save"></i> Save Changes
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
                    <i class="fas fa-exclamation-triangle"></i> Confirm Deactivation
                </h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form action="${pageContext.request.contextPath}/machines" method="POST">
                <input type="hidden" name="action" value="deactivate">
                <input type="hidden" name="id" id="deactivateId">
                <div class="modal-body">
                    <div class="text-center mb-3">
                        <i class="fas fa-power-off fa-3x text-danger"></i>
                    </div>
                    <p class="text-center">
                        Are you sure you want to deactivate machine:<br>
                        <strong id="deactivateMachineName">-</strong>?
                    </p>
                  
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cancel
                    </button>
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-power-off"></i> Deactivate
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<!-- Logout Modal -->
<% if (odIsLoggedIn) { %>
<%@ include file="/view/authen/logout.jsp" %>
<% } %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
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
            
            window.location.href = contextPath + '/machines?' + params.toString();
        }

        // Detail button click handlers
        document.querySelectorAll('.btn-detail').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                loadMachineDetail(id);
            });
        });

        function loadMachineDetail(id) {
            fetch(contextPath + '/machines?action=detail&id=' + id)
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
                            '<span class="text-success"><i class="fas fa-check"></i> Yes</span>' : 
                            '<span class="text-muted"><i class="fas fa-times"></i> No</span>';
                        document.getElementById('detailCreatedAt').textContent = m.createdAt || '-';
                        document.getElementById('detailDescription').textContent = m.description || '-';
                        
                        new bootstrap.Modal(document.getElementById('detailModal')).show();
                    } else {
                        alert('Error: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to load machine details');
                });
        }

        function getStatusBadge(status) {
            if (status === 'ACTIVE') return '<span class="badge bg-success">Active</span>';
            if (status === 'INACTIVE') return '<span class="badge bg-danger">Inactive</span>';
            return '<span class="badge bg-secondary">Discontinued</span>';
        }

        // Edit button click handlers
        document.querySelectorAll('.btn-edit').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const id = this.getAttribute('data-id');
                loadMachineForEdit(id);
            });
        });

        function loadMachineForEdit(id) {
            fetch(contextPath + '/machines?action=detail&id=' + id)
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
                        alert('Error: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Failed to load machine for editing');
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
