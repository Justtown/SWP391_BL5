<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sản phẩm của tôi - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: #f8f9fa;
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
        .status-ACTIVE {
            background-color: #28a745;
            color: #fff;
        }
        .status-FINISHED {
            background-color: #6c757d;
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
        .page-header {
            background: white;
            border-bottom: 1px solid #dee2e6;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .breadcrumb {
            margin-bottom: 0;
        }
        
        .breadcrumb-item a {
            color: #0d6efd;
            text-decoration: none;
        }
        
        .breadcrumb-item a:hover {
            text-decoration: underline;
        }

        .content-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 1.5rem;
            margin-bottom: 1.5rem;
        }
        
        .stats-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 1rem;
            margin-bottom: 1.5rem;
        }
        
        .stats-card h6 {
            color: #6c757d;
            font-size: 0.875rem;
            margin-bottom: 0.5rem;
        }
        
        .stats-card .number {
            font-size: 1.5rem;
            font-weight: 600;
            color: #212529;
        }
    </style>
</head>
<body>
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <div class="main-content">
        <!-- Page Header -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1">
                    <i class="fas fa-box me-2"></i>
                    Sản phẩm của tôi
                </h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/customer/dashboard">Dashboard</a>
                        </li>
                        <li class="breadcrumb-item active">Sản phẩm</li>
                    </ol>
                </nav>
            </div>
            <div class="d-flex align-items-center">
                <span>
                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
                </span>
            </div>
        </div>
        
        <!-- Content -->
        <div class="container-fluid">
            <!-- Statistics Cards -->
            <div class="row mb-3">
                <div class="col-md-4">
                    <div class="stats-card">
                        <h6><i class="fas fa-cogs me-2"></i>Đang thuê</h6>
                        <div class="number text-success">${activeCount != null ? activeCount : 0}</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stats-card">
                        <h6><i class="fas fa-check-circle me-2"></i>Đã thuê</h6>
                        <div class="number text-secondary">${finishedCount != null ? finishedCount : 0}</div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="stats-card">
                        <h6><i class="fas fa-boxes me-2"></i>Tổng số</h6>
                        <div class="number text-primary">${totalItems != null ? totalItems : 0}</div>
                    </div>
                </div>
            </div>
            
            <div class="content-card">
            
            <!-- Filter & Search Section -->
            <div class="filter-section">
                <select class="form-select filter-dropdown" id="statusFilter">
                    <option value="All" ${statusFilter == 'All' ? 'selected' : ''}>Tất cả</option>
                    <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Đang thuê</option>
                    <option value="FINISHED" ${statusFilter == 'FINISHED' ? 'selected' : ''}>Đã thuê</option>
                </select>
                
                <div class="search-container">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" class="form-control search-input" id="keyword" 
                           placeholder="Tìm kiếm theo serial number, model, brand, mã hợp đồng..." 
                           value="${keyword != null ? keyword : ''}">
                    <i class="fas fa-times clear-search" id="clearSearch"></i>
                </div>
                
            </div>
            
            <!-- Pagination Info -->
            <c:if test="${not empty totalItems}">
                <div class="pagination-info">
                    Hiển thị ${startIndex + 1} đến ${startIndex + products.size()} trong tổng số ${totalItems} sản phẩm
                </div>
            </c:if>
            
            <!-- Product Table -->
            <div class="table-container">
                <table class="table table-striped table-hover">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Serial Number</th>
                            <th>Model</th>
                            <th>Brand</th>
                            <th>Loại máy</th>
                            <th>Mã hợp đồng</th>
                            <th>Trạng thái</th>
                            <th>Giá thuê</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty products && products.size() > 0}">
                                <c:forEach var="product" items="${products}" varStatus="loop">
                                    <tr>
                                        <td>${startIndex + loop.index + 1}</td>
                                        <td><strong>${product.serialNumber != null ? product.serialNumber : 'N/A'}</strong></td>
                                        <td>${product.modelName != null ? product.modelName : 'N/A'}</td>
                                        <td>${product.brand != null ? product.brand : 'N/A'}</td>
                                        <td>${product.typeName != null ? product.typeName : 'N/A'}</td>
                                        <td>${product.contractCode != null ? product.contractCode : 'N/A'}</td>
                                        <td>
                                            <span class="status-badge status-${product.contractStatus}">
                                                <c:choose>
                                                    <c:when test="${product.contractStatus == 'ACTIVE'}">Đang thuê</c:when>
                                                    <c:when test="${product.contractStatus == 'FINISHED'}">Đã thuê</c:when>
                                                    <c:otherwise>${product.contractStatus}</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td class="text-success fw-semibold">
                                            <c:choose>
                                                <c:when test="${not empty product.price}">
                                                    <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="đ" groupingUsed="true"/>
                                                </c:when>
                                                <c:otherwise>N/A</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/customer/products?action=detail&id=${product.id}" 
                                               class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i> Xem
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" class="text-center text-muted py-4">
                                        <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                        Không có sản phẩm nào
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
                if (status && status !== 'All') {
                    params.append('status', status);
                }
                if (keyword) {
                    params.append('keyword', keyword);
                }
                params.append('page', '1');
                
                const queryString = params.toString();
                const url = '${pageContext.request.contextPath}/customer/products' + (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
            
            function goToPage(page) {
                const status = statusFilter.value;
                const keyword = keywordInput.value.trim();
                
                const params = new URLSearchParams();
                if (status && status !== 'All') {
                    params.append('status', status);
                }
                if (keyword) {
                    params.append('keyword', keyword);
                }
                params.append('page', page);
                
                const queryString = params.toString();
                const url = '${pageContext.request.contextPath}/customer/products' + (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
        });
    </script>
</body>
</html>
