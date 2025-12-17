<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add new user - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .add-user-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 600px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 1.75rem;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
            color: #333;
        }
        .form-label {
            font-weight: 500;
            color: #495057;
            margin-bottom: 8px;
        }
        .form-control, .form-select {
            border-radius: 5px;
            padding: 8px 12px;
        }
        .status-buttons {
            display: flex;
            gap: 10px;
        }
        .status-btn {
            flex: 1;
            padding: 8px 16px;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            background: white;
            color: #495057;
            cursor: pointer;
            transition: all 0.3s;
            text-align: center;
        }
        .status-btn.active {
            background-color: #28a745;
            color: white;
            border-color: #28a745;
        }
        .status-btn:not(.active) {
            background-color: white;
            color: #495057;
            border-color: #dee2e6;
        }
        .error-message {
            color: #dc3545;
            font-size: 0.875rem;
            margin-top: 5px;
        }
        .btn-add {
            width: 100%;
            padding: 10px;
            background-color: #0d6efd;
            border-color: #0d6efd;
            color: white;
            font-weight: 500;
            border-radius: 5px;
        }
        .btn-add:hover {
            background-color: #0b5ed7;
            border-color: #0a58ca;
            color: white;
        }
        .date-input-wrapper {
            position: relative;
        }
        .date-icon {
            position: absolute;
            right: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
            pointer-events: none;
        }
        .btn-back {
            background-color: #6c757d;
            border-color: #6c757d;
            color: white;
            padding: 10px 20px;
            font-weight: 500;
            border-radius: 5px;
            text-decoration: none;
            display: inline-block;
        }
        .btn-back:hover {
            background-color: #5c636a;
            border-color: #565e64;
            color: white;
        }
        .btn-group-custom {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="add-user-container">
            <h2 class="page-title">Add new user</h2>
            
            <form action="${pageContext.request.contextPath}/add-user" method="POST" id="addUserForm">
                
                <!-- Full name -->
                <div class="mb-3">
                    <label for="fullName" class="form-label">Full name</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" 
                           value="${fullName != null ? fullName : ''}" required>
                </div>
                
                <!-- Email -->
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" 
                           value="${email != null ? email : ''}" required>
                </div>
                
                <!-- Username (Optional) -->
                <div class="mb-3">
                    <label for="username" class="form-label">Username </label>
                    <input type="text" class="form-control" id="username" name="username" 
                           value="${username != null ? username : ''}" 
                           maxlength="100">
                </div>
                
                <!-- Phone -->
                <div class="mb-3">
                    <label for="phone" class="form-label">Phone</label>
                    <input type="tel" class="form-control" id="phone" name="phone" 
                           value="${phone != null ? phone : ''}">
                </div>
                
                <!-- Address -->
                <div class="mb-3">
                    <label for="address" class="form-label">Address</label>
                    <textarea class="form-control" id="address" name="address" rows="3" 
                              maxlength="500">${address != null ? address : ''}</textarea>
                    <small class="text-muted">Maximum 500 characters</small>
                </div>
                
                <!-- DOB -->
                <div class="mb-3">
                    <label for="dob" class="form-label">DOB</label>
                    <div class="date-input-wrapper">
                        <input type="date" class="form-control" id="dob" name="dob" 
                               value="${dob != null ? dob : ''}">
                        <i class="fas fa-calendar-alt date-icon"></i>
                    </div>
                </div>
                
                <!-- Password -->
                <div class="mb-3">
                    <label for="password" class="form-label">Password</label>
                    <input type="password" class="form-control" id="password" name="password" 
                           required minlength="6">
                </div>
                
                <!-- Role -->
                <div class="mb-3">
                    <label for="role" class="form-label">Role</label>
                    <select class="form-select" id="role" name="role" required>
                        <option value="">Select Role</option>
                        <c:forEach var="role" items="${roles}">
                            <option value="${role}" ${selectedRole == role ? 'selected' : ''}>${role}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <!-- Status -->
                <div class="mb-3">
                    <label class="form-label">Status</label>
                    <div class="status-buttons">
                        <input type="hidden" id="status" name="status" value="${status != null ? status : '1'}">
                        <button type="button" class="status-btn ${status == '1' || status == null ? 'active' : 'inactive'}" 
                                onclick="setStatus('1', this)">
                            active
                        </button>
                        <button type="button" class="status-btn ${status == '0' ? 'active' : 'inactive'}" 
                                onclick="setStatus('0', this)">
                            inactive
                        </button>
                    </div>
                </div>
                
                <!-- Error message -->
                <c:if test="${not empty errorMessage}">
                    <div class="error-message">
                        ${errorMessage}
                    </div>
                </c:if>
                
                <!-- Buttons -->
                <div class="btn-group-custom">
                    <a href="${pageContext.request.contextPath}/manage-account" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <button type="submit" class="btn btn-add">Add</button>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function setStatus(value, clickedBtn) {
            document.getElementById('status').value = value;
            const buttons = document.querySelectorAll('.status-btn');
            buttons.forEach(btn => {
                btn.classList.remove('active');
                if (btn === clickedBtn) {
                    btn.classList.add('active');
                }
            });
        }
        
        // Form validation
        document.getElementById('addUserForm').addEventListener('submit', function(e) {
            const fullName = document.getElementById('fullName').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const role = document.getElementById('role').value;
            
            if (!fullName) {
                e.preventDefault();
                alert('Full name is required!');
                return false;
            }
            
            if (!email) {
                e.preventDefault();
                alert('Email is required!');
                return false;
            }
            
            if (!password || password.length < 6) {
                e.preventDefault();
                alert('Password must be at least 6 characters!');
                return false;
            }
            
            if (!role) {
                e.preventDefault();
                alert('Please select a role!');
                return false;
            }
        });
    </script>
</body>
</html>

