<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Information - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .user-info-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 800px;
            margin: 0 auto;
        }
        .page-title {
            font-size: 1.75rem;
            font-weight: bold;
            margin-bottom: 30px;
            text-align: center;
            color: #333;
        }
        .info-section {
            margin-bottom: 30px;
        }
        .info-section-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e9ecef;
        }
        .info-row {
            display: flex;
            margin-bottom: 15px;
            padding: 10px 0;
            border-bottom: 1px solid #f0f0f0;
        }
        .info-label {
            font-weight: 600;
            color: #6c757d;
            width: 150px;
            flex-shrink: 0;
        }
        .info-value {
            color: #212529;
            flex: 1;
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
        .form-control:disabled {
            background-color: #e9ecef;
            cursor: not-allowed;
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
            padding: 10px;
            background-color: #f8d7da;
            border-radius: 5px;
        }
        .success-message {
            color: #155724;
            font-size: 0.875rem;
            margin-top: 5px;
            padding: 10px;
            background-color: #d4edda;
            border-radius: 5px;
        }
        .btn-update {
            background-color: #0d6efd;
            border-color: #0d6efd;
            color: white;
            padding: 10px 20px;
            font-weight: 500;
            border-radius: 5px;
        }
        .btn-update:hover {
            background-color: #0b5ed7;
            border-color: #0a58ca;
            color: white;
        }
        .btn-back {
            background-color: #6c757d;
            border-color: #6c757d;
            color: white;
            padding: 10px 20px;
            font-weight: 500;
            border-radius: 5px;
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
            margin-top: 30px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="user-info-container">
            <h2 class="page-title">User Information</h2>
            
            <!-- Success/Error Messages -->
            <c:if test="${not empty param.success}">
                <div class="success-message">
                    <i class="fas fa-check-circle"></i> ${param.success}
                </div>
            </c:if>
            
            <c:if test="${not empty errorMessage}">
                <div class="error-message">
                    <i class="fas fa-exclamation-circle"></i> ${errorMessage}
                </div>
            </c:if>
            
            <c:if test="${not empty user}">
                <!-- User Information Section (Read-only) -->
                <div class="info-section">
                    <h3 class="info-section-title">User Details</h3>
                    
                    <div class="info-row">
                        <div class="info-label">ID:</div>
                        <div class="info-value">${user.id}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Username:</div>
                        <div class="info-value">${user.username != null ? user.username : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Full Name:</div>
                        <div class="info-value">${user.fullName != null ? user.fullName : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Email:</div>
                        <div class="info-value">${user.email != null ? user.email : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Phone:</div>
                        <div class="info-value">${user.phoneNumber != null ? user.phoneNumber : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Address:</div>
                        <div class="info-value">${user.address != null ? user.address : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Date of Birth:</div>
                        <div class="info-value">${user.birthdate != null ? user.birthdate : 'N/A'}</div>
                    </div>
                    
                    <div class="info-row">
                        <div class="info-label">Created At:</div>
                        <div class="info-value">${user.createdAt != null ? user.createdAt : 'N/A'}</div>
                    </div>
                </div>
                
                <!-- Update Section (Role and Status) -->
                <div class="info-section">
                    <h3 class="info-section-title">Update Role & Status</h3>
                    
                    <form action="${pageContext.request.contextPath}/user-info" method="POST" id="updateUserForm">
                        <input type="hidden" name="userId" value="${user.id}">
                        <input type="hidden" name="action" value="update">
                        
                        <!-- Role -->
                        <div class="mb-3">
                            <label for="role" class="form-label">Role</label>
                            <select class="form-select" id="role" name="role" required>
                                <option value="">Select Role</option>
                                <c:forEach var="role" items="${roles}">
                                    <option value="${role}" ${user.roleName == role ? 'selected' : ''}>${role}</option>
                                </c:forEach>
                            </select>
                        </div>
                        
                        <!-- Status -->
                        <div class="mb-3">
                            <label class="form-label">Status</label>
                            <div class="status-buttons">
                                <input type="hidden" id="status" name="status" value="${user.status != null ? user.status : '1'}">
                                <button type="button" class="status-btn ${user.status == 1 ? 'active' : ''}" 
                                        onclick="setStatus('1', this)">
                                    Active
                                </button>
                                <button type="button" class="status-btn ${user.status == 0 ? 'active' : ''}" 
                                        onclick="setStatus('0', this)">
                                    Inactive
                                </button>
                            </div>
                        </div>
                        
                        <!-- Buttons -->
                        <div class="btn-group-custom">
                            <a href="${pageContext.request.contextPath}/admin/manage-account" class="btn btn-back">
                                <i class="fas fa-arrow-left"></i> Back
                            </a>
                            <button type="submit" class="btn btn-update">
                                <i class="fas fa-save"></i> Update
                            </button>
                        </div>
                    </form>
                </div>
            </c:if>
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
        document.getElementById('updateUserForm').addEventListener('submit', function(e) {
            const role = document.getElementById('role').value;
            
            if (!role) {
                e.preventDefault();
                alert('Please select a role!');
                return false;
            }
        });
    </script>
</body>
</html>

