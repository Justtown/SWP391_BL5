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

        .form-card {
            max-width: 600px;
            margin: 0 auto;
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
    <!-- Sidebar + layout -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <!-- Page Header (match other management pages) -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-user-plus me-2"></i>Thêm user mới</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/${sessionScope.roleName}/dashboard">Dashboard</a>
                        </li>
                        <li class="breadcrumb-item">
                            <a href="${pageContext.request.contextPath}/admin/manage-account">Users</a>
                        </li>
                        <li class="breadcrumb-item active">Add</li>
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

        <div class="container-fluid">
        <div class="content-card form-card">
            
            <form action="${pageContext.request.contextPath}/add-user" method="POST" id="addUserForm" autocomplete="off">
                
                <!-- Full name -->
                <div class="mb-3">
                    <label for="fullName" class="form-label">Full name</label>
                    <input type="text" class="form-control" id="fullName" name="fullName" 
                           value="${requestScope.fullName != null ? requestScope.fullName : ''}" required autocomplete="off">
                </div>
                
                <!-- Email -->
                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" 
                           value="${requestScope.email != null ? requestScope.email : ''}"
                           required
                           autocomplete="off">
                </div>
                
                <!-- Username (Optional) -->
                <div class="mb-3">
                    <label for="username" class="form-label">Username </label>
                    <input type="text" class="form-control" id="username" name="username" 
                           value="${requestScope.username != null ? requestScope.username : ''}" 
                           minlength="3"
                           maxlength="100"
                           autocomplete="off">
                    <small class="text-muted">Để trống nếu muốn hệ thống tự tạo từ email</small>
                </div>
                
                <!-- Phone -->
                <div class="mb-3">
                    <label for="phone" class="form-label">Phone</label>
                    <input type="tel" class="form-control" id="phone" name="phone" 
                           value="${requestScope.phone != null ? requestScope.phone : ''}" autocomplete="off">
                </div>
                
                <!-- Address -->
                <div class="mb-3">
                    <label for="address" class="form-label">Address</label>
                    <textarea class="form-control" id="address" name="address" rows="3" 
                              maxlength="500" autocomplete="off">${requestScope.address != null ? requestScope.address : ''}</textarea>
                    <small class="text-muted">Maximum 500 characters</small>
                </div>
                
                <!-- DOB -->
                <div class="mb-3">
                    <label for="dob" class="form-label">DOB</label>
                    <div class="date-input-wrapper">
                        <input type="date" class="form-control" id="dob" name="dob" 
                               value="${requestScope.dob != null ? requestScope.dob : ''}" autocomplete="off">
                        <i class="fas fa-calendar-alt date-icon"></i>
                    </div>
                    <small class="text-muted">Ngày sinh phải nhỏ hơn ngày hiện tại</small>
                </div>
                
                <!-- Password -->
                <div class="mb-3">
                    <label for="password" class="form-label">Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="password" name="password" 
                           required minlength="6" autocomplete="new-password">
                </div>
                
                <!-- Confirm Password -->
                <div class="mb-3">
                    <label for="confirmPassword" class="form-label">Confirm Password <span class="text-danger">*</span></label>
                    <input type="password" class="form-control" id="confirmPassword" name="confirmPassword" 
                           required minlength="6" autocomplete="new-password">
                    <div class="invalid-feedback" id="passwordMismatchError" style="display: none;">
                        Passwords do not match!
                    </div>
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
                        <input type="hidden" id="status" name="status" value="${requestScope.status != null ? requestScope.status : '1'}">
                        <button type="button" class="status-btn ${requestScope.status == '1' || requestScope.status == null ? 'active' : 'inactive'}" 
                                onclick="setStatus('1', this)">
                            Active
                        </button>
                        <button type="button" class="status-btn ${requestScope.status == '0' ? 'active' : 'inactive'}" 
                                onclick="setStatus('0', this)">
                            Deactive
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
                    <a href="${pageContext.request.contextPath}/admin/manage-account" class="btn-back">
                        <i class="fas fa-arrow-left"></i> Back
                    </a>
                    <button type="submit" class="btn btn-add">Add</button>
                </div>
            </form>
        </div>
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
        
        // ===== Validators (client-side) =====
        const form = document.getElementById('addUserForm');
        const emailInput = document.getElementById('email');
        const usernameInput = document.getElementById('username');
        const dobInput = document.getElementById('dob');

        const EMAIL_REGEX = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        const USERNAME_REGEX = /^[A-Za-z0-9._-]{3,100}$/;

        function formatDateYYYYMMDD(dateObj) {
            const yyyy = dateObj.getFullYear();
            const mm = String(dateObj.getMonth() + 1).padStart(2, '0');
            const dd = String(dateObj.getDate()).padStart(2, '0');
            return `${yyyy}-${mm}-${dd}`;
        }

        function validateEmail() {
            if (!emailInput) return true;
            const v = emailInput.value.trim();
            if (!v) {
                emailInput.setCustomValidity('');
                return true;
            }
            if (!EMAIL_REGEX.test(v)) {
                emailInput.setCustomValidity('Email không hợp lệ');
                return false;
            }
            emailInput.setCustomValidity('');
            return true;
        }

        function validateUsername() {
            if (!usernameInput) return true;
            const v = usernameInput.value.trim();
            if (!v) {
                usernameInput.setCustomValidity('');
                return true;
            }
            if (!USERNAME_REGEX.test(v)) {
                usernameInput.setCustomValidity('Username không hợp lệ (3-100 ký tự, chỉ gồm chữ/số và . _ -)');
                return false;
            }
            usernameInput.setCustomValidity('');
            return true;
        }

        function validateDob() {
            if (!dobInput) return true;
            const v = dobInput.value;
            if (!v) {
                dobInput.setCustomValidity('');
                return true;
            }
            // Compare by Date objects to avoid locale/format edge cases
            const selected = new Date(v + 'T00:00:00');
            const today = new Date();
            today.setHours(0, 0, 0, 0);
            if (Number.isNaN(selected.getTime())) {
                dobInput.setCustomValidity('Ngày sinh không hợp lệ');
                return false;
            }
            if (selected.getTime() >= today.getTime()) {
                dobInput.setCustomValidity('Ngày sinh phải nhỏ hơn ngày hiện tại');
                return false;
            }
            dobInput.setCustomValidity('');
            return true;
        }

        // Prevent selecting future/today date in picker (still validate manually on submit)
        if (dobInput) {
            const maxDob = new Date();
            maxDob.setDate(maxDob.getDate() - 1); // must be strictly before today
            dobInput.setAttribute('max', formatDateYYYYMMDD(maxDob));
            dobInput.addEventListener('change', validateDob);
            dobInput.addEventListener('input', validateDob);
            // initial validation for prefilled values
            validateDob();
        }

        if (emailInput) {
            emailInput.addEventListener('input', validateEmail);
            emailInput.addEventListener('change', validateEmail);
            validateEmail();
        }

        if (usernameInput) {
            usernameInput.addEventListener('input', validateUsername);
            usernameInput.addEventListener('change', validateUsername);
            validateUsername();
        }

        // ===== Password match validation =====
        const passwordInput = document.getElementById('password');
        const confirmPasswordInput = document.getElementById('confirmPassword');
        const passwordMismatchError = document.getElementById('passwordMismatchError');
        
        function validatePasswordMatch() {
            const password = passwordInput.value;
            const confirmPassword = confirmPasswordInput.value;
            
            if (confirmPassword && password !== confirmPassword) {
                confirmPasswordInput.classList.add('is-invalid');
                passwordMismatchError.style.display = 'block';
                confirmPasswordInput.setCustomValidity('Passwords do not match');
                return false;
            } else {
                confirmPasswordInput.classList.remove('is-invalid');
                passwordMismatchError.style.display = 'none';
                confirmPasswordInput.setCustomValidity('');
                return true;
            }
        }
        
        confirmPasswordInput.addEventListener('input', validatePasswordMatch);
        passwordInput.addEventListener('input', function() {
            if (confirmPasswordInput.value) {
                validatePasswordMatch();
            }
        });
        
        // Form validation
        if (form) {
            form.addEventListener('submit', function(e) {
                // run custom validators (DOB/email/username)
                validateEmail();
                validateUsername();
                validateDob();
                validatePasswordMatch();

                if (!form.checkValidity()) {
                    e.preventDefault();
                    form.reportValidity();
                    return false;
                }
                return true;
            });
        }
    </script>
</body>
</html>

