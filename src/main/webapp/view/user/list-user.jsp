<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>List User - Argo Machine Management</title>
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
        
        .search-container {
            margin-bottom: 20px;
            display: flex;
            gap: 10px;
        }
        
        .search-container input {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .search-container button {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .search-container button:hover {
            background-color: #0056b3;
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
        <h1>List role</h1>
        
        <!-- Search Form -->
        <form method="GET" action="${pageContext.request.contextPath}/list-user" class="search-container">
            <input 
                type="text" 
                name="keyword" 
                placeholder="Enter keyword to search"
                value="${keyword != null ? keyword : ''}">
            <button type="submit">Search</button>
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
    </script>
</body>
</html>

