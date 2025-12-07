<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Register - Argo Machine Management</title>

        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

        <style>
            body {
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
            }

            .register-card {
                border-radius: 10px;
                box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
            }

            .phone-group {
                display: flex;
                gap: 0;
            }

            .phone-code {
                width: 90px;
                flex-shrink: 0;
                border-top-right-radius: 0;
                border-bottom-right-radius: 0;
            }

            .phone-input {
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }

            /* Custom validation feedback for input-group */
            .input-group .form-control.is-invalid,
            .input-group .form-select.is-invalid {
                border-color: #dc3545;
            }

            /* Error message styling */
            .error-message {
                display: none;
                color: #dc3545;
                font-size: 0.875em;
                margin-top: 0.25rem;
            }

            .error-message.show {
                display: block;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-6 col-lg-5">
                    <div class="card register-card">
                        <div class="card-body p-5">
                            <!-- Header -->
                            <h2 class="text-center mb-2">Register</h2>
                            <p class="text-center text-muted mb-4">
                                Already have an account? <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">Login</a>
                            </p>

                            <!-- Error Message -->
                            <% if (request.getAttribute("error") != null) { %>
                            <div class="alert alert-danger" role="alert">
                                <i class="fas fa-exclamation-circle"></i>
                                <%= request.getAttribute("error") %>
                            </div>
                            <% } %>

                            <!-- Register Form -->
                            <form action="${pageContext.request.contextPath}/register" method="POST" id="registerForm" novalidate>
                                
                                <!-- Username -->
                                <div class="mb-3">
                                    <label for="username" class="form-label">Username</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-user"></i>
                                        </span>
                                        <input 
                                            type="text" 
                                            class="form-control" 
                                            id="username" 
                                            name="username" 
                                            placeholder="your_username"
                                            value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : "" %>"
                                            required>
                                        <div class="invalid-feedback">Vui lòng nhập username</div>
                                    </div>
                                    <div class="error-message" id="usernameError"></div>
                                </div>

                                <!-- Full Name -->
                                <div class="mb-3">
                                    <label for="fullName" class="form-label">Full Name</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-id-card"></i>
                                        </span>
                                        <input 
                                            type="text" 
                                            class="form-control" 
                                            id="fullName" 
                                            name="fullName" 
                                            placeholder="Nguyen Van A"
                                            value="<%= request.getAttribute("fullName") != null ? request.getAttribute("fullName") : "" %>"
                                            required>
                                        <div class="invalid-feedback">Vui lòng nhập họ tên</div>
                                    </div>
                                    <div class="error-message" id="fullNameError"></div>
                                </div>

                                <!-- Email -->
                                <div class="mb-3">
                                    <label for="email" class="form-label">Email</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-envelope"></i>
                                        </span>
                                        <input 
                                            type="email" 
                                            class="form-control" 
                                            id="email" 
                                            name="email" 
                                            placeholder="a@gmail.com"
                                            value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : "" %>"
                                            required>
                                        <div class="invalid-feedback">Vui lòng nhập email hợp lệ</div>
                                    </div>
                                    <div class="error-message" id="emailError"></div>
                                </div>

                                <!-- Phone Number -->
                                <div class="mb-3">
                                    <label for="phoneNumber" class="form-label">Phone number</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-phone"></i>
                                        </span>
                                        <select class="form-select phone-code" name="phoneCode" id="phoneCode">
                                            <option value="+84" <%= "+84".equals(request.getAttribute("phoneCode")) || request.getAttribute("phoneCode") == null ? "selected" : "" %>>+84</option>
                                            <option value="+1" <%= "+1".equals(request.getAttribute("phoneCode")) ? "selected" : "" %>>+1</option>
                                            <option value="+44" <%= "+44".equals(request.getAttribute("phoneCode")) ? "selected" : "" %>>+44</option>
                                            <option value="+81" <%= "+81".equals(request.getAttribute("phoneCode")) ? "selected" : "" %>>+81</option>
                                            <option value="+82" <%= "+82".equals(request.getAttribute("phoneCode")) ? "selected" : "" %>>+82</option>
                                            <option value="+86" <%= "+86".equals(request.getAttribute("phoneCode")) ? "selected" : "" %>>+86</option>
                                        </select>
                                        <input 
                                            type="tel" 
                                            class="form-control phone-input" 
                                            id="phoneNumber" 
                                            name="phoneNumber" 
                                            placeholder="091234567"
                                            value="<%= request.getAttribute("phoneNumber") != null ? request.getAttribute("phoneNumber") : "" %>"
                                            pattern="[0-9]{9,15}">
                                        <div class="invalid-feedback">Số điện thoại không hợp lệ</div>
                                    </div>
                                    <div class="error-message" id="phoneError"></div>
                                </div>

                                <!-- Address -->
                                <div class="mb-3">
                                    <label for="address" class="form-label">Address</label>
                                    <div class="input-group">
                                        <span class="input-group-text">
                                            <i class="fas fa-map-marker-alt"></i>
                                        </span>
                                        <input 
                                            type="text" 
                                            class="form-control" 
                                            id="address" 
                                            name="address" 
                                            placeholder="Ho Guom, Ha Noi"
                                            value="<%= request.getAttribute("address") != null ? request.getAttribute("address") : "" %>">
                                    </div>
                                </div>

                                <!-- Password -->
                                <div class="mb-3">
                                    <label for="password" class="form-label">Password</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-lock"></i>
                                        </span>
                                        <input 
                                            type="password" 
                                            class="form-control" 
                                            id="password" 
                                            name="password" 
                                            placeholder="**********"
                                            minlength="6"
                                            required>
                                        <div class="invalid-feedback">Mật khẩu phải có ít nhất 6 ký tự</div>
                                    </div>
                                    <div class="error-message" id="passwordError"></div>
                                </div>

                                <!-- Repeat Password -->
                                <div class="mb-3">
                                    <label for="confirmPassword" class="form-label">Repeat password</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-lock"></i>
                                        </span>
                                        <input 
                                            type="password" 
                                            class="form-control" 
                                            id="confirmPassword" 
                                            name="confirmPassword" 
                                            placeholder="**********"
                                            required>
                                        <div class="invalid-feedback">Mật khẩu xác nhận không khớp</div>
                                    </div>
                                    <div class="error-message" id="confirmPasswordError"></div>
                                </div>

                                <!-- Birthdate -->
                                <div class="mb-3">
                                    <label for="birthdate" class="form-label">Birthdate</label>
                                    <div class="input-group has-validation">
                                        <span class="input-group-text">
                                            <i class="fas fa-calendar-alt"></i>
                                        </span>
                                        <input 
                                            type="date" 
                                            class="form-control" 
                                            id="birthdate" 
                                            name="birthdate"
                                            value="<%= request.getAttribute("birthdate") != null ? request.getAttribute("birthdate") : "" %>">
                                        <div class="invalid-feedback">Ngày sinh không hợp lệ</div>
                                    </div>
                                    <div class="error-message" id="birthdateError"></div>
                                </div>

                                <!-- Buttons -->
                                <div class="d-flex justify-content-between align-items-center mb-2 gap-2">
                                    <button type="submit" class="btn btn-primary w-50">
                                        <i class="fas fa-user-plus"></i> Sign Up
                                    </button>
                                    <a href="${pageContext.request.contextPath}/login" class="btn btn-outline-primary w-50">
                                        <i class="fas fa-sign-in-alt"></i> Login
                                    </a>
                                </div>

                                <!-- Google Sign Up Button -->
                                <button type="button" class="btn btn-outline-secondary w-100">
                                    <i class="fab fa-google"></i> Sign up with Google
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Bootstrap 5 JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <!-- Form Validation Script -->
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const form = document.getElementById('registerForm');
                const username = document.getElementById('username');
                const fullName = document.getElementById('fullName');
                const password = document.getElementById('password');
                const confirmPassword = document.getElementById('confirmPassword');
                const email = document.getElementById('email');
                const phoneNumber = document.getElementById('phoneNumber');
                const birthdate = document.getElementById('birthdate');
                
                // Helper function to show error
                function showError(input, errorElement, message) {
                    input.classList.add('is-invalid');
                    if (errorElement) {
                        errorElement.textContent = message;
                        errorElement.classList.add('show');
                    }
                }
                
                // Helper function to hide error
                function hideError(input, errorElement) {
                    input.classList.remove('is-invalid');
                    if (errorElement) {
                        errorElement.textContent = '';
                        errorElement.classList.remove('show');
                    }
                }
                
                // Username validation
                username.addEventListener('input', function() {
                    const errorEl = document.getElementById('usernameError');
                    if (!this.value.trim()) {
                        showError(this, errorEl, 'Vui lòng nhập username');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Full name validation
                fullName.addEventListener('input', function() {
                    const errorEl = document.getElementById('fullNameError');
                    if (!this.value.trim()) {
                        showError(this, errorEl, 'Vui lòng nhập họ tên');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Real-time password match validation
                confirmPassword.addEventListener('input', function() {
                    const errorEl = document.getElementById('confirmPasswordError');
                    if (this.value !== password.value) {
                        showError(this, errorEl, 'Mật khẩu xác nhận không khớp');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                password.addEventListener('input', function() {
                    const errorEl = document.getElementById('passwordError');
                    const confirmErrorEl = document.getElementById('confirmPasswordError');
                    
                    // Revalidate confirm password when password changes
                    if (confirmPassword.value && confirmPassword.value !== this.value) {
                        showError(confirmPassword, confirmErrorEl, 'Mật khẩu xác nhận không khớp');
                    } else if (confirmPassword.value) {
                        hideError(confirmPassword, confirmErrorEl);
                    }
                    
                    // Password strength check
                    if (this.value.length < 6 && this.value.length > 0) {
                        showError(this, errorEl, 'Mật khẩu phải có ít nhất 6 ký tự');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Email validation
                email.addEventListener('input', function() {
                    const errorEl = document.getElementById('emailError');
                    const emailPattern = /^[A-Za-z0-9+_.-]+@(.+)$/;
                    if (this.value && !emailPattern.test(this.value)) {
                        showError(this, errorEl, 'Email không hợp lệ');
                    } else if (!this.value.trim()) {
                        showError(this, errorEl, 'Vui lòng nhập email');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Phone validation
                phoneNumber.addEventListener('input', function() {
                    const errorEl = document.getElementById('phoneError');
                    const phonePattern = /^[0-9]{9,15}$/;
                    if (this.value && !phonePattern.test(this.value)) {
                        showError(this, errorEl, 'Số điện thoại không hợp lệ (9-15 số)');
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Birthdate validation - cannot be in the future
                birthdate.addEventListener('input', function() {
                    const errorEl = document.getElementById('birthdateError');
                    if (this.value) {
                        const selectedDate = new Date(this.value);
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);
                        
                        if (selectedDate > today) {
                            showError(this, errorEl, 'Ngày sinh không thể là ngày trong tương lai');
                        } else {
                            hideError(this, errorEl);
                        }
                    } else {
                        hideError(this, errorEl);
                    }
                });
                
                // Form submission validation
                form.addEventListener('submit', function(event) {
                    let isValid = true;
                    
                    // Username validation
                    if (!username.value.trim()) {
                        showError(username, document.getElementById('usernameError'), 'Vui lòng nhập username');
                        isValid = false;
                    }
                    
                    // Full name validation
                    if (!fullName.value.trim()) {
                        showError(fullName, document.getElementById('fullNameError'), 'Vui lòng nhập họ tên');
                        isValid = false;
                    }
                    
                    // Email validation
                    const emailPattern = /^[A-Za-z0-9+_.-]+@(.+)$/;
                    if (!email.value.trim()) {
                        showError(email, document.getElementById('emailError'), 'Vui lòng nhập email');
                        isValid = false;
                    } else if (!emailPattern.test(email.value)) {
                        showError(email, document.getElementById('emailError'), 'Email không hợp lệ');
                        isValid = false;
                    }
                    
                    // Password validation
                    if (!password.value) {
                        showError(password, document.getElementById('passwordError'), 'Vui lòng nhập mật khẩu');
                        isValid = false;
                    } else if (password.value.length < 6) {
                        showError(password, document.getElementById('passwordError'), 'Mật khẩu phải có ít nhất 6 ký tự');
                        isValid = false;
                    }
                    
                    // Confirm password validation
                    if (!confirmPassword.value) {
                        showError(confirmPassword, document.getElementById('confirmPasswordError'), 'Vui lòng xác nhận mật khẩu');
                        isValid = false;
                    } else if (password.value !== confirmPassword.value) {
                        showError(confirmPassword, document.getElementById('confirmPasswordError'), 'Mật khẩu xác nhận không khớp');
                        isValid = false;
                    }
                    
                    // Phone validation (optional but must be valid if provided)
                    const phonePattern = /^[0-9]{9,15}$/;
                    if (phoneNumber.value && !phonePattern.test(phoneNumber.value)) {
                        showError(phoneNumber, document.getElementById('phoneError'), 'Số điện thoại không hợp lệ (9-15 số)');
                        isValid = false;
                    }
                    
                    // Birthdate validation
                    if (birthdate.value) {
                        const selectedDate = new Date(birthdate.value);
                        const today = new Date();
                        today.setHours(0, 0, 0, 0);
                        if (selectedDate > today) {
                            showError(birthdate, document.getElementById('birthdateError'), 'Ngày sinh không thể là ngày trong tương lai');
                            isValid = false;
                        }
                    }
                    
                    if (!isValid) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                });
            });
        </script>
    </body>
</html>
