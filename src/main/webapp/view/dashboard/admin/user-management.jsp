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
            background-color: #f5f5f5;
            padding: 20px;
        }
        .user-management-container {
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
            width: 120px;
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
        .add-user-link {
            color: #0d6efd;
            text-decoration: none;
            font-weight: 500;
            white-space: nowrap;
            flex-shrink: 0;
        }
        .add-user-link:hover {
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
        .status-active {
            color: #28a745;
            font-weight: 500;
        }
        .status-deactive {
            color: #dc3545;
            font-weight: 500;
        }
        .btn-update {
            background-color: #20c997;
            border-color: #20c997;
            color: white;
            padding: 5px 15px;
            font-size: 0.875rem;
        }
        .btn-update:hover {
            background-color: #1aa179;
            border-color: #1aa179;
            color: white;
        }
        .no-users-message {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
    </style>
</head>
<body>
    <div class="container-fluid" style="max-width: 100%; overflow-x: hidden;">
        <div class="user-management-container">
            <h1 class="page-title">User Management</h1>
            
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
                
                <a href="${pageContext.request.contextPath}/add-user" class="add-user-link">
                    <i class="fas fa-plus"></i> Add new user
                </a>
            </div>
            
            <!-- User Table -->
            <div class="table-container">
                <table class="table table-hover">
                    <thead>
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
                                        <td>${loop.index + 1}</td>
                                        <td>${user.fullName != null ? user.fullName : user.username}</td>
                                        <td>${user.roleName != null ? user.roleName : 'N/A'}</td>
                                        <td>${user.email != null ? user.email : 'N/A'}</td>
                                        <td>
                                            <span class="${user.status == 1 ? 'status-active' : 'status-deactive'}">
                                                ${user.status == 1 ? 'Active' : 'Deactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <button type="button" class="btn btn-update btn-sm" 
                                                    onclick="updateUser(${user.id})">
                                                Update
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
                    applyFilters();
                }
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
                
                const queryString = params.toString();
                const url = '${pageContext.request.contextPath}/manage-account' + 
                           (queryString ? '?' + queryString : '');
                window.location.href = url;
            }
        });
        
        // Update user function
        function updateUser(userId) {
            window.location.href = '${pageContext.request.contextPath}/user-info?id=' + userId;
        }
    </script>
</body>
</html>

