<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Management - Argo Machine Management</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f5f5;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        h1 {
            margin-bottom: 20px;
            color: #333;
        }
        
        .filter-container {
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
            align-items: center;
            flex-wrap: wrap;
        }
        
        .filter-group {
            display: flex;
            align-items: center;
            gap: 5px;
        }
        
        .filter-dropdown {
            padding: 8px 35px 8px 12px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
            background-color: white;
            cursor: pointer;
            appearance: none;
            background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23333' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: right 10px center;
            background-size: 12px;
        }
        
        .search-container {
            flex: 1;
            position: relative;
            min-width: 200px;
        }
        
        .search-container input {
            width: 100%;
            padding: 8px 35px 8px 35px;
            border: 1px solid #ddd;
            border-radius: 20px;
            font-size: 14px;
        }
        
        .search-icon {
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #999;
            pointer-events: none;
        }
        
        .search-clear {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            background: none;
            border: none;
            color: #999;
            cursor: pointer;
            font-size: 16px;
            padding: 0;
            width: 20px;
            height: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .search-clear:hover {
            color: #333;
        }
        
        .add-user-link {
            color: #007bff;
            text-decoration: underline;
            font-size: 14px;
            cursor: pointer;
        }
        
        .add-user-link:hover {
            color: #0056b3;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        
        th {
            background-color: #f8f9fa;
            font-weight: bold;
            color: #333;
        }
        
        tr:hover {
            background-color: #f5f5f5;
        }
        
        .status-active {
            color: #28a745;
            font-weight: bold;
        }
        
        .status-deactive {
            color: #dc3545;
            font-weight: bold;
        }
        
        .btn-update {
            padding: 6px 12px;
            background-color: #17a2b8;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
        }
        
        .btn-update:hover {
            background-color: #138496;
        }
        
        .no-data {
            text-align: center;
            padding: 40px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>User Management</h1>
        
        <!-- Filter and Search Form -->
        <form method="GET" action="${pageContext.request.contextPath}/list-user" class="filter-container" id="filterForm">
            <!-- Role Filter -->
            <div class="filter-group">
                <select name="role" class="filter-dropdown" onchange="document.getElementById('filterForm').submit();">
                    <option value="all" ${selectedRole == 'all' ? 'selected' : ''}>All Role</option>
                    <c:forEach var="role" items="${roles}">
                        <option value="${role}" ${selectedRole == role ? 'selected' : ''}>${role}</option>
                    </c:forEach>
                </select>
            </div>
            
            <!-- Status Filter -->
            <div class="filter-group">
                <select name="status" class="filter-dropdown" onchange="document.getElementById('filterForm').submit();">
                    <option value="all" ${selectedStatus == 'all' ? 'selected' : ''}>All Status</option>
                    <option value="1" ${selectedStatus == '1' ? 'selected' : ''}>Active</option>
                    <option value="0" ${selectedStatus == '0' ? 'selected' : ''}>Deactive</option>
                </select>
            </div>
            
            <!-- Search Bar -->
            <div class="search-container">
                <span class="search-icon">🔍</span>
                <input 
                    type="text" 
                    name="keyword" 
                    id="searchInput"
                    placeholder="search"
                    value="${keyword}"
                    onkeypress="if(event.key === 'Enter') document.getElementById('filterForm').submit();">
                <c:if test="${keyword != null && !keyword.isEmpty()}">
                    <button type="button" class="search-clear" onclick="clearSearch()" title="Clear search">×</button>
                </c:if>
            </div>
            
            <!-- Add New User Link -->
            <a href="#" class="add-user-link" onclick="addNewUser(); return false;">Add new user</a>
        </form>
        
        <!-- User Table -->
        <table>
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
                    <c:when test="${users != null && !users.isEmpty()}">
                        <c:forEach var="user" items="${users}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${user.fullName != null ? user.fullName : user.username}</td>
                                <td>${user.roleName}</td>
                                <td>${user.email}</td>
                                <td>
                                    <span class="${user.status == 1 ? 'status-active' : 'status-deactive'}">
                                        ${user.status == 1 ? 'Active' : 'Deactive'}
                                    </span>
                                </td>
                                <td>
                                    <button class="btn-update" onclick="updateUser(${user.id})">Update</button>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="no-data">No users found</td>
                        </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>
    
    <script>
        function updateUser(userId) {
            // TODO: Implement update user functionality
            alert('Update user ID: ' + userId);
        }
        
        function clearSearch() {
            document.getElementById('searchInput').value = '';
            // Remove keyword from URL and submit
            const form = document.getElementById('filterForm');
            const url = new URL(window.location.href);
            url.searchParams.delete('keyword');
            window.location.href = url.toString();
        }
        
        function addNewUser() {
            // TODO: Implement add new user functionality
            alert('Add new user - Coming soon!');
        }
    </script>
</body>
</html>

