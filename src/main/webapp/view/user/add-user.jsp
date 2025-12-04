<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add new user - Argo Machine Management</title>
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
            max-width: 600px;
            margin: 0 auto;
            background-color: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        h1 {
            margin-bottom: 30px;
            color: #333;
            text-align: center;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        label {
            display: block;
            margin-bottom: 5px;
            color: #333;
            font-weight: bold;
        }
        
        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="date"],
        input[type="password"],
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .status-group {
            display: flex;
            gap: 10px;
            margin-top: 5px;
        }
        
        .status-btn {
            flex: 1;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            background-color: white;
            cursor: pointer;
            font-size: 14px;
            text-align: center;
        }
        
        .status-btn.active {
            background-color: #28a745;
            color: white;
            border-color: #28a745;
        }
        
        .status-btn.inactive {
            background-color: #dc3545;
            color: white;
            border-color: #dc3545;
        }
        
        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }
        
        .btn-add {
            flex: 1;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            font-weight: bold;
        }
        
        .btn-add:hover {
            background-color: #0056b3;
        }
        
        .back-link {
            color: #007bff;
            text-decoration: underline;
            font-size: 14px;
            cursor: pointer;
            text-align: right;
            display: block;
            margin-top: 15px;
        }
        
        .back-link:hover {
            color: #0056b3;
        }
        
        .error-message {
            color: #dc3545;
            font-size: 14px;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Add new user</h1>
        
        <form method="POST" action="${pageContext.request.contextPath}/add-user" id="addUserForm">
            <div class="form-group">
                <label for="fullName">Full name</label>
                <input type="text" id="fullName" name="fullName" required>
            </div>
            
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <div class="form-group">
                <label for="phone">Phone</label>
                <input type="tel" id="phone" name="phone">
            </div>
            
            <div class="form-group">
                <label for="dob">DOB</label>
                <input type="date" id="dob" name="dob">
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="role">Role</label>
                <select id="role" name="role" required>
                    <option value="">Select Role</option>
                    <c:forEach var="role" items="${roles}">
                        <option value="${role}">${role}</option>
                    </c:forEach>
                </select>
            </div>
            
            <div class="form-group">
                <label>Status</label>
                <div class="status-group">
                    <input type="hidden" id="status" name="status" value="1">
                    <button type="button" class="status-btn active" onclick="setStatus(1)">active</button>
                    <button type="button" class="status-btn" onclick="setStatus(0)">inactive</button>
                </div>
            </div>
            
            <c:if test="${not empty errorMessage}">
                <div class="error-message">${errorMessage}</div>
            </c:if>
            
            <div class="btn-group">
                <button type="submit" class="btn-add">Add</button>
            </div>
        </form>
        
        <a href="${pageContext.request.contextPath}/list-user" class="back-link">Back to user list</a>
    </div>
    
    <script>
        function setStatus(status) {
            document.getElementById('status').value = status;
            const buttons = document.querySelectorAll('.status-btn');
            buttons.forEach(btn => {
                btn.classList.remove('active', 'inactive');
            });
            if (status == 1) {
                buttons[0].classList.add('active');
                buttons[1].classList.remove('active', 'inactive');
            } else {
                buttons[1].classList.add('inactive');
                buttons[0].classList.remove('active', 'inactive');
            }
        }
    </script>
</body>
</html>

