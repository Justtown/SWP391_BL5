<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Argo Machine Management</title>
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
            width: 120px;
            flex-shrink: 0;
        }
        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 200px;
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
        .status-active {
            color: #28a745;
            font-weight: 500;
        }
        .status-deactive {
            color: #dc3545;
            font-weight: 500;
        }
        .no-users-message {
            text-align: center;
            padding: 40px;
            color: #6c757d;
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
    <!-- Sidebar + layout -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <!-- Page Header (match other management pages) -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-users me-2"></i>Quản lý User</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/${sessionScope.roleName}/dashboard">Dashboard</a>
                        </li>
                        <li class="breadcrumb-item active">Users</li>
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

        <div class="container-fluid" style="max-width: 100%; overflow-x: hidden;">
        <div class="content-card">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h5 class="mb-0">Danh sách user</h5>
                <button type="button" class="btn btn-primary"
                        onclick="window.location.href='${pageContext.request.contextPath}/admin/add-user'">
                    <i class="fas fa-plus"></i> Thêm user mới
                </button>
            </div>
            
            <!-- Success/Error Messages -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>${param.success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            
            <!-- Filter and Search Section -->
            <div class="filter-section">
                <select class="form-select filter-dropdown" id="roleFilter" name="role">
                    <option value="All Role" ${roleFilter == 'All Role' ? 'selected' : ''}>All Role</option>
                    <option value="admin" ${roleFilter == 'admin' ? 'selected' : ''}>Admin</option>
                    <option value="manager" ${roleFilter == 'manager' ? 'selected' : ''}>Manager</option>
                    <option value="sale" ${roleFilter == 'sale' ? 'selected' : ''}>Sale</option>
                    <option value="customer" ${roleFilter == 'customer' ? 'selected' : ''}>Customer</option>
                </select>
                
                <select class="form-select filter-dropdown" id="statusFilter" name="status">
                    <option value="All Status" ${statusFilter == 'All Status' ? 'selected' : ''}>All Status</option>
                    <option value="Active" ${statusFilter == 'Active' ? 'selected' : ''}>Active</option>
                    <option value="Deactive" ${statusFilter == 'Deactive' ? 'selected' : ''}>Deactive</option>
                </select>
                
                <div class="search-container">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" class="form-control search-input" id="keyword" 
                           name="keyword" placeholder="Search..." 
                           value="${keyword != null ? keyword : ''}">
                    <i class="fas fa-times clear-search" id="clearSearch"></i>
                </div>
            </div>
            
            <!-- Pagination Info -->
            <c:if test="${not empty totalUsers}">
                <div class="pagination-info">
                    Showing ${startIndex + 1} to ${startIndex + users.size()} of ${totalUsers} users
                </div>
            </c:if>
            
            <!-- User Table -->
            <div class="table-container">
                <table class="table table-bordered table-hover">
                    <thead class="table-light">
                        <tr>
                            <th>STT</th>
                            <th>Name</th>
                            <th>Role</th>
                            <th>Gmail</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty users && users.size() > 0}">
                                <c:forEach var="user" items="${users}" varStatus="loop">
                                    <tr>
                                        <td>${startIndex + loop.index + 1}</td>
                                        <td>${user.fullName != null ? user.fullName : user.username}</td>
                                        <td>${user.roleName != null ? user.roleName : 'N/A'}</td>
                                        <td>${user.email != null ? user.email : 'N/A'}</td>
                                        <td>
                                            <span class="${user.status == 1 ? 'status-active' : 'status-deactive'}">
                                                ${user.status == 1 ? 'Active' : 'Deactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <button type="button" class="btn btn-sm btn-warning"
                                                    data-user-id="${user.id}">
                                                <i class="fas fa-edit"></i> Update
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="no-users-message">
                                        No users found
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
        // Filter and search functionality
        document.addEventListener('DOMContentLoaded', function() {
            const roleFilter = document.getElementById('roleFilter');
            const statusFilter = document.getElementById('statusFilter');
            const keywordInput = document.getElementById('keyword');
            const clearSearch = document.getElementById('clearSearch');
            
            // Show/hide clear button
            keywordInput.addEventListener('input', function() {
                if (this.value.trim().length > 0) {
                    clearSearch.style.display = 'block';
                } else {
                    clearSearch.style.display = 'none';
                }
            });
            
            // Check initial state
            if (keywordInput.value.trim().length > 0) {
                clearSearch.style.display = 'block';
            }
            
            // Clear search
            clearSearch.addEventListener('click', function() {
                keywordInput.value = '';
                clearSearch.style.display = 'none';
                applyFilters();
            });
            
            // Apply filters on change
            roleFilter.addEventListener('change', applyFilters);
            statusFilter.addEventListener('change', applyFilters);
            
            // Apply filters on Enter key in search
            keywordInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    applyFilters();
                }
            });
            
            // Pagination button click handlers
            document.querySelectorAll('.pagination-btn[data-page]').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const page = this.getAttribute('data-page');
                    goToPage(parseInt(page));
                });
            });
            
            function applyFilters() {
                const role = roleFilter.value;
                const status = statusFilter.value;
                const keyword = keywordInput.value.trim();
                
                const params = new URLSearchParams();
                if (role && role !== 'All Role') {
                    params.append('role', role);
                }
                if (status && status !== 'All Status') {
                    params.append('status', status);
                }
                if (keyword) {
                    params.append('keyword', keyword);
                }
                // Reset to page 1 when filtering
                params.append('page', '1');
                
                const queryString = params.toString();
                const url = '${pageContext.request.contextPath}/admin/manage-account' + 
                           (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
            
            // Go to specific page
            function goToPage(page) {
                const role = roleFilter.value;
                const status = statusFilter.value;
                const keyword = keywordInput.value.trim();
                
                const params = new URLSearchParams();
                if (role && role !== 'All Role') {
                    params.append('role', role);
                }
                if (status && status !== 'All Status') {
                    params.append('status', status);
                }
                if (keyword) {
                    params.append('keyword', keyword);
                }
                params.append('page', page);
                
                const queryString = params.toString();
                const url = '${pageContext.request.contextPath}/admin/manage-account' + 
                           (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
        });
        
        // Update user button click handlers
        document.querySelectorAll('button[data-user-id]').forEach(function(btn) {
            btn.addEventListener('click', function() {
                const userId = this.getAttribute('data-user-id');
                window.location.href = '${pageContext.request.contextPath}/user-info?id=' + userId;
            });
        });
    </script>
</body>
</html>

