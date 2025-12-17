<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Contracts - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #121212;
            color: #f8f9fa;
        }
        .my-contracts-container {
            background: #1e1e1e;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.6);
            padding: 30px;
            max-width: 100%;
            box-sizing: border-box;
        }
        .page-title {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 5px;
            text-align: center; /* UPDATED: center title */
        }
        .page-subtitle {
            font-size: 0.95rem;
            color: #adb5bd;
            margin-bottom: 25px;
            text-align: center; /* UPDATED: center subtitle (optional but nice) */
        }
        .filter-section {
            display: flex;
            gap: 10px;
            margin-bottom: 25px;
            flex-wrap: nowrap;
            align-items: center;
            width: 100%;
        }
        .filter-dropdown {
            width: 160px;
            flex-shrink: 0;
        }
        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 0;
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
        .clear-search {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            cursor: pointer;
            color: #6c757d;
            display: none;
        }
        .clear-search:hover {
            color: #dc3545;
        }
        .table-container {
            overflow-x: auto;
        }
        .table {
            margin-bottom: 0;
        }
        .table thead th {
            background-color: #212529;
            border-bottom: 2px solid #343a40;
            color: #f8f9fa;
            font-weight: 500;
        }
        .table tbody tr {
            background-color: #1e1e1e;
        }
        .table tbody tr:nth-child(even) {
            background-color: #242424;
        }
        .table tbody tr:hover {
            background-color: #2c2c2c;
        }
        .status-badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        .status-DRAFT {
            background-color: #ffc107;
            color: #000;
        }
        .status-ACTIVE {
            background-color: #28a745;
            color: #fff;
        }
        .status-FINISHED {
            background-color: #6c757d;
            color: #fff;
        }
        .status-CANCELLED {
            background-color: #dc3545;
            color: #fff;
        }
        .btn-view {
            background-color: #0dcaf0;
            color: #000;
            border: none;
            padding: 4px 10px;
            font-size: 0.8rem;
        }
        .btn-view:hover {
            background-color: #31d2f2;
            color: #000;
        }
        .pagination-info {
            font-size: 0.9rem;
            color: #adb5bd;
            margin-bottom: 10px;
        }
        .pagination-container {
            display: flex;
            justify-content: center;
            margin-top: 20px;
            gap: 5px;
        }
        .pagination-btn {
            border: 1px solid #495057;
            padding: 6px 12px;
            border-radius: 5px;
            color: #f8f9fa;
            text-decoration: none;
            font-size: 0.9rem;
            background-color: #1e1e1e;
        }
        .pagination-btn.active {
            background-color: #0d6efd;
            border-color: #0d6efd;
        }
        .pagination-btn.disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }
        .empty-state {
            text-align: center;
            padding: 40px 20px;
            color: #adb5bd;
        }
        .empty-state i {
            font-size: 2rem;
            margin-bottom: 10px;
        }

        /* UPDATED: force contract code link to be white (normal/hover/visited) */
        .contract-code-link,
        .contract-code-link:hover,
        .contract-code-link:visited {
            color: #f8f9fa !important;
        }
    </style>
</head>
<body>
<!-- Sidebar -->
<jsp:include page="/view/common/dashboard/sideBar.jsp" />

<!-- Main Content -->
<div class="main-content">
    <div class="container-fluid" style="max-width: 100%; overflow-x: hidden;">
        <div class="my-contracts-container">
            <h1 class="page-title">My Contracts</h1>
            <div class="page-subtitle">
                View your contracts. For changes, please contact Sales/Manager.
            </div>

            <!-- Filter Section -->
            <div class="filter-section">
                <select class="form-select filter-dropdown" id="statusFilter">
                    <option value="All Status" ${statusFilter == 'All Status' ? 'selected' : ''}>All Status</option>
                    <option value="DRAFT" ${statusFilter == 'DRAFT' ? 'selected' : ''}>DRAFT</option>
                    <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>ACTIVE</option>
                    <option value="FINISHED" ${statusFilter == 'FINISHED' ? 'selected' : ''}>FINISHED</option>
                    <option value="CANCELLED" ${statusFilter == 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                </select>

                <div class="search-container">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" class="form-control search-input" id="keyword"
                           placeholder="Search by contract code..."
                           value="${keyword != null ? keyword : ''}">
                    <i class="fas fa-times clear-search" id="clearSearch"></i>
                </div>
            </div>

            <!-- Pagination Info -->
            <c:if test="${not empty totalContracts}">
                <div class="pagination-info">
                    Showing ${startIndex + 1} to ${startIndex + contracts.size()} of ${totalContracts} contracts
                </div>
            </c:if>

            <!-- Contracts Table -->
            <div class="table-container">
                <table class="table table-dark table-hover align-middle">
                    <thead>
                    <tr>
                        <th style="width: 60px;">#</th>
                        <th>Contract Code</th>
                        <th>Start Date</th>
                        <th>End Date</th>
                        <th>Status</th>
                        <th>Manager</th>
                        <th>Created At</th>
                        <th style="width: 90px;">Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty contracts && contracts.size() > 0}">
                            <c:forEach var="contract" items="${contracts}" varStatus="loop">
                                <tr>
                                    <td>${startIndex + loop.index + 1}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/customer/contracts?action=detail&id=${contract.id}"
                                           class="text-decoration-none contract-code-link">
                                            <strong>${contract.contractCode}</strong>
                                        </a>
                                    </td>
                                    <td>${contract.startDate}</td>
                                    <td>${contract.endDate != null ? contract.endDate : 'N/A'}</td>
                                    <td>
                                                <span class="status-badge status-${contract.status}">
                                                        ${contract.status}
                                                </span>
                                    </td>
                                    <td>${contract.managerName != null ? contract.managerName : 'N/A'}</td>
                                    <td>${contract.createdAt != null ? contract.createdAt : 'N/A'}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/customer/contracts?action=detail&id=${contract.id}"
                                           class="btn btn-view btn-sm">
                                            <i class="fas fa-eye"></i> View
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="8" class="empty-state">
                                    <i class="fas fa-file-contract mb-2"></i>
                                    <div>You have no contracts yet. Please contact Sales to create a contract.</div>
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
                    <!-- Previous -->
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

                    <!-- Page numbers -->
                    <c:forEach var="i" begin="1" end="${totalPages}">
                        <c:choose>
                            <c:when test="${i == currentPage}">
                                <span class="pagination-btn active">${i}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="#" class="pagination-btn" data-page="${i}">${i}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <!-- Next -->
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        const statusFilter = document.getElementById('statusFilter');
        const keywordInput = document.getElementById('keyword');
        const clearSearch = document.getElementById('clearSearch');

        // Show/hide clear button
        keywordInput.addEventListener('input', function() {
            clearSearch.style.display = this.value.trim() ? 'block' : 'none';
        });

        clearSearch.addEventListener('click', function() {
            keywordInput.value = '';
            clearSearch.style.display = 'none';
            applyFilters();
        });

        // Apply filters on change / enter
        statusFilter.addEventListener('change', applyFilters);
        keywordInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                applyFilters();
            }
        });

        // Pagination button handlers
        document.querySelectorAll('.pagination-btn[data-page]').forEach(function(btn) {
            btn.addEventListener('click', function(e) {
                e.preventDefault();
                const page = this.getAttribute('data-page');
                goToPage(parseInt(page));
            });
        });

            function applyFilters() {
            const status = statusFilter.value;
            const keyword = keywordInput.value.trim();

            const params = new URLSearchParams();
            if (status && status !== 'All Status') {
                params.append('status', status);
            }
            if (keyword) {
                params.append('keyword', keyword);
            }
            params.append('page', '1');

            const queryString = params.toString();
            const url = '${pageContext.request.contextPath}/customer/contracts' +
                (queryString ? '?' + queryString : '');
            window.location.href = url;
        }

        function goToPage(page) {
            const status = statusFilter.value;
            const keyword = keywordInput.value.trim();

            const params = new URLSearchParams();
            if (status && status !== 'All Status') {
                params.append('status', status);
            }
            if (keyword) {
                params.append('keyword', keyword);
            }
            params.append('page', page);

            const queryString = params.toString();
            const url = '${pageContext.request.contextPath}/customer/contracts' +
                (queryString ? '?' + queryString : '');
            window.location.href = url;
        }
    });
</script>
</body>
</html>
