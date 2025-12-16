<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Contract Management - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .contract-management-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 100%;
            box-sizing: border-box;
        }
        .page-title {
            font-size: 2rem;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
        }
        .filter-section {
            display: flex;
            gap: 10px;
            margin-bottom: 30px;
            flex-wrap: nowrap;
            align-items: center;
            width: 100%;
        }
        .filter-dropdown {
            width: 150px;
            flex-shrink: 0;
        }
        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 0;
        }
        .search-input {
            padding-left: 40px;
            padding-right: 40px;
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
        .add-contract-link {
            color: #0d6efd;
            text-decoration: none;
            font-weight: 500;
            white-space: nowrap;
            flex-shrink: 0;
        }
        .add-contract-link:hover {
            text-decoration: underline;
        }
        .table-container {
            overflow-x: auto;
        }
        .table {
            margin-bottom: 0;
        }
        .table thead th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
        }
        .status-badge {
            padding: 4px 12px;
            border-radius: 12px;
            font-size: 0.875rem;
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
        .pagination-info {
            margin-bottom: 15px;
            color: #6c757d;
            font-size: 0.9rem;
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
        .pagination-btn.active:hover {
            background-color: #0b5ed7;
            border-color: #0a58ca;
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <div class="container-fluid" style="max-width: 100%; overflow-x: hidden;">
            <div class="contract-management-container">
                <h1 class="page-title">Contract Management</h1>
            
            <!-- Filter & Search Section -->
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
                           placeholder="Search by contract code, customer, manager..." 
                           value="${keyword != null ? keyword : ''}">
                    <i class="fas fa-times clear-search" id="clearSearch"></i>
                </div>
                
                <a href="${pageContext.request.contextPath}/contracts?action=create" class="add-contract-link">
                    <i class="fas fa-plus"></i> Create New Contract
                </a>
            </div>
            
            <!-- Pagination Info -->
            <c:if test="${not empty totalContracts}">
                <div class="pagination-info">
                    Showing ${startIndex + 1} to ${startIndex + contracts.size()} of ${totalContracts} contracts
                </div>
            </c:if>
            
            <!-- Contract Table -->
            <div class="table-container">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Contract Code</th>
                            <th>Customer</th>
                            <th>Manager</th>
                            <th>Start Date</th>
                            <th>End Date</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty contracts && contracts.size() > 0}">
                                <c:forEach var="contract" items="${contracts}" varStatus="loop">
                                    <tr>
                                        <td>${startIndex + loop.index + 1}</td>
                                        <td><strong>${contract.contractCode}</strong></td>
                                        <td>${contract.customerName != null ? contract.customerName : 'N/A'}</td>
                                        <td>${contract.managerName != null ? contract.managerName : 'N/A'}</td>
                                        <td>${contract.startDate}</td>
                                        <td>${contract.endDate}</td>
                                        <td>
                                            <span class="status-badge status-${contract.status}">
                                                ${contract.status}
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/contracts?action=detail&id=${contract.id}" 
                                               class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i> View
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="8" class="text-center">No contracts found</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
            
            <!-- Pagination -->
            <c:if test="${not empty totalPages && totalPages > 1}">
                <div class="pagination-container">
                    <!-- Previous Button -->
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
                    
                    <!-- Page Numbers -->
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
                    
                    <!-- Next Button -->
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
            
            // Apply filters on change
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
                const url = '${pageContext.request.contextPath}/contracts' + 
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
                const url = '${pageContext.request.contextPath}/contracts' + 
                           (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
        });
    </script>

    </div> <!-- end main-content -->
</body>
</html>

