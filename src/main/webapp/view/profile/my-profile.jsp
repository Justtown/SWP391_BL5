<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
            padding: 20px;
        }
        .profile-card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 800px;
            margin: 0 auto;
        }
        .profile-header {
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 2px solid #e9ecef;
            padding-bottom: 20px;
            position: relative;
        }
        .edit-button-top {
            position: absolute;
            top: 0;
            right: 0;
        }
        .edit-button-top .btn {
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
            font-weight: 600;
            padding: 8px 16px;
            transition: all 0.3s ease;
        }
        .edit-button-top .btn:hover {
            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);
            transform: translateY(-2px);
            background-color: #0056b3;
            border-color: #0056b3;
        }
        .edit-button-top .btn i {
            margin-right: 5px;
        }
        .avatar-container {
            position: relative;
            display: inline-block;
            margin-bottom: 15px;
        }
        .avatar-placeholder {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 48px;
            border: 4px solid #fff;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }
        .avatar-image {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            border: 4px solid #fff;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
        }
        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
        }
        .form-control:disabled,
        .form-control[readonly] {
            background-color: #f8f9fa;
            cursor: default;
            border: none;
            padding-left: 0;
        }
        .view-mode .form-control {
            background-color: transparent;
            border: none;
            padding-left: 0;
            font-weight: 500;
        }
        .btn-group-custom {
            display: flex;
            gap: 10px;
            justify-content: flex-end;
            margin-top: 30px;
        }
        .info-row {
            padding: 12px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 5px;
        }
        .info-value {
            color: #212529;
        }
        .alert {
            margin-bottom: 20px;
        }
        .text-danger {
            color: #dc3545 !important;
        }
        .form-text {
            font-size: 0.875rem;
            margin-top: 0.25rem;
        }
        .was-validated .form-control:invalid,
        .form-control.is-invalid {
            border-color: #dc3545;
        }
        .was-validated .form-control:valid,
        .form-control.is-valid {
            border-color: #28a745;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="profile-card">
            <!-- isEditMode được set từ controller (mặc định là false - view mode) -->
            
            <div class="profile-header">
                <h2 class="mb-4">MY PROFILE</h2>
                
                <!-- Edit Profile Button ở góc trên (chỉ hiển thị khi ở chế độ view) -->
                <c:if test="${isEditMode != true}">
                    <div class="edit-button-top">
                        <a href="${pageContext.request.contextPath}/my-profile?mode=edit" class="btn btn-primary btn-sm">
                            <i class="fas fa-edit"></i> Edit Profile
                        </a>
                    </div>
                </c:if>
                
                <!-- Avatar -->
                <div class="avatar-container">
                    <label class="form-label">Avatar</label>
                    <c:choose>
                        <c:when test="${not empty profile.avatar and profile.avatar != ''}">
                            <img src="${profile.avatar}" alt="Avatar" class="avatar-image" id="avatarPreview">
                        </c:when>
                        <c:otherwise>
                            <div class="avatar-placeholder">
                                <i class="fas fa-user"></i>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Messages -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <c:choose>
                <c:when test="${isEditMode == true}">
                    <!-- Profile Form - Edit Mode -->
                    <form action="${pageContext.request.contextPath}/my-profile" method="POST" id="profileForm">
                        <input type="hidden" name="action" value="update">
                        
                        <div class="row">
                            <!-- Name -->
                            <div class="col-md-12 mb-3">
                                <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="name" name="name" 
                                       value="${profile.name}" required minlength="2" maxlength="100"
                                       title="Tên phải có từ 2-100 ký tự">
                                <div class="invalid-feedback">Tên phải có từ 2-100 ký tự!</div>
                            </div>

                            <!-- Email -->
                            <div class="col-md-12 mb-3">
                                <label for="email" class="form-label">Email <span class="text-danger">*</span></label>
                                <input type="email" class="form-control" id="email" name="email" 
                                       value="${profile.email}" required maxlength="255"
                                       pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"
                                       title="Email không đúng định dạng! Ví dụ: example@gmail.com">
                                <div class="invalid-feedback">Email không đúng định dạng!</div>
                            </div>

                            <!-- Phone -->
                            <div class="col-md-12 mb-3">
                                <label for="phone" class="form-label">Phone</label>
                                <input type="tel" class="form-control" id="phone" name="phone" 
                                       value="${profile.phone}" maxlength="15"
                                       placeholder="0123456789 hoặc +84123456789"
                                       title="Số điện thoại 10 số (bắt đầu bằng 0) hoặc 11 số">
                                <small class="form-text text-muted">Ví dụ: 0123456789 (10 số) hoặc 84123456789 (11 số)</small>
                                <div class="invalid-feedback">Số điện thoại phải có 10 hoặc 11 chữ số!</div>
                            </div>

                            <!-- Address -->
                            <div class="col-md-12 mb-3">
                                <label for="address" class="form-label">Address</label>
                                <textarea class="form-control" id="address" name="address" rows="3" 
                                          maxlength="500">${profile.address}</textarea>
                                <small class="form-text text-muted"><span id="addressCount">0</span>/500 ký tự</small>
                                <div class="invalid-feedback">Địa chỉ không được vượt quá 500 ký tự!</div>
                            </div>

                            <!-- Role (Read-only) -->
                            <div class="col-md-12 mb-3">
                                <label for="role" class="form-label">Role</label>
                                <input type="text" class="form-control" id="role" 
                                       value="${profile.roleName != null ? profile.roleName : 'Customer'}" 
                                       disabled>
                            </div>

                            <!-- Birthdate -->
                            <div class="col-md-12 mb-3">
                                <label for="birthdate" class="form-label">Birthdate</label>
                                <input type="date" class="form-control" id="birthdate" name="birthdate" 
                                       value="${profile.birthdate}" max="">
                                <small class="form-text text-muted">Bạn phải ít nhất 13 tuổi</small>
                                <div class="invalid-feedback">Ngày sinh không hợp lệ!</div>
                            </div>

                            <!-- Avatar URL (Hidden) -->
                            <div class="col-md-12 mb-3" style="display: none;">
                                <label for="avatar" class="form-label">Avatar URL</label>
                                <input type="text" class="form-control" id="avatar" name="avatar" 
                                       value="${profile.avatar}" placeholder="Enter image URL">
                            </div>
                        </div>

                        <!-- Buttons -->
                        <div class="btn-group-custom">
                            <button type="button" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/my-profile'">
                                <i class="fas fa-times"></i> Cancel
                            </button>
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Save
                            </button>
                        </div>
                    </form>
                </c:when>
                <c:otherwise>
                    <!-- Profile View Mode -->
                    <div class="view-mode">
                        <div class="info-row">
                            <div class="info-label">Name</div>
                            <div class="info-value">${profile.name}</div>
                        </div>
                        
                        <div class="info-row">
                            <div class="info-label">Email</div>
                            <div class="info-value">${profile.email}</div>
                        </div>
                        
                        <div class="info-row">
                            <div class="info-label">Phone</div>
                            <div class="info-value">${not empty profile.phone ? profile.phone : 'N/A'}</div>
                        </div>
                        
                        <div class="info-row">
                            <div class="info-label">Address</div>
                            <div class="info-value">${not empty profile.address ? profile.address : 'N/A'}</div>
                        </div>
                        
                        <div class="info-row">
                            <div class="info-label">Role</div>
                            <div class="info-value">${profile.roleName != null ? profile.roleName : 'Customer'}</div>
                        </div>
                        
                        <div class="info-row">
                            <div class="info-label">Birthdate</div>
                            <div class="info-value">${not empty profile.birthdate ? profile.birthdate : 'N/A'}</div>
                        </div>
                    </div>

                    <!-- Buttons -->
                    <div class="btn-group-custom">
                        <button type="button" class="btn btn-secondary" onclick="window.location.href='${pageContext.request.contextPath}/home'">
                            <i class="fas fa-arrow-left"></i> Back
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Chỉ chạy JavaScript khi ở chế độ edit
        <c:if test="${isEditMode == true}">
        document.addEventListener('DOMContentLoaded', function() {
            // Set max date for birthdate (today - 13 years)
            const birthdateInput = document.getElementById('birthdate');
            if (birthdateInput) {
                const today = new Date();
                const maxDate = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
                birthdateInput.max = maxDate.toISOString().split('T')[0];
            }
            
            // Address character counter
            const addressTextarea = document.getElementById('address');
            const addressCount = document.getElementById('addressCount');
            if (addressTextarea && addressCount) {
                addressCount.textContent = addressTextarea.value.length;
                addressTextarea.addEventListener('input', function() {
                    addressCount.textContent = this.value.length;
                    if (this.value.length > 500) {
                        this.classList.add('is-invalid');
                    } else {
                        this.classList.remove('is-invalid');
                    }
                });
            }

            // Avatar preview (if URL is entered)
            const avatarInput = document.getElementById('avatar');
            if (avatarInput) {
                avatarInput.addEventListener('input', function(e) {
                    const url = e.target.value;
                    const preview = document.getElementById('avatarPreview');
                    if (url && preview) {
                        preview.src = url;
                        preview.style.display = 'block';
                        const placeholder = document.querySelector('.avatar-placeholder');
                        if (placeholder) {
                            placeholder.style.display = 'none';
                        }
                    }
                });
            }

            // Form validation
            (function() {
                'use strict';
                const form = document.getElementById('profileForm');
                if (!form) return;
                
                form.addEventListener('submit', function(event) {
                    if (!form.checkValidity()) {
                        event.preventDefault();
                        event.stopPropagation();
                    }
                    
                    // Custom validation
                    const name = document.getElementById('name').value.trim();
                    const email = document.getElementById('email').value.trim();
                    const phone = document.getElementById('phone').value.trim();
                    const address = document.getElementById('address').value;
                    const birthdate = document.getElementById('birthdate').value;
                    
                    let isValid = true;
                    let errorMessage = '';
                    
                    // Validate Name
                    if (name.length < 2 || name.length > 100) {
                        isValid = false;
                        errorMessage = 'Tên phải có từ 2-100 ký tự!';
                    }
                    
                    // Validate Email
                    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                    if (!emailRegex.test(email)) {
                        isValid = false;
                        errorMessage = 'Email không đúng định dạng! Ví dụ: example@gmail.com';
                    }
                    
                    // Validate Phone (if provided) - Số điện thoại Việt Nam
                    if (phone && phone.trim()) {
                        const phoneDigits = phone.replace(/[^0-9]/g, '');
                        if (phoneDigits.length === 10) {
                            // Phải bắt đầu bằng 0
                            if (!phoneDigits.startsWith('0')) {
                                isValid = false;
                                errorMessage = 'Số điện thoại 10 số phải bắt đầu bằng 0!';
                            }
                        } else if (phoneDigits.length === 11) {
                            // Phải bắt đầu bằng 0 hoặc 84
                            if (!phoneDigits.startsWith('0') && !phoneDigits.startsWith('84')) {
                                isValid = false;
                                errorMessage = 'Số điện thoại 11 số phải bắt đầu bằng 0 hoặc 84!';
                            }
                        } else {
                            isValid = false;
                            errorMessage = 'Số điện thoại phải có 10 hoặc 11 chữ số!';
                        }
                    }
                    
                    // Validate Address
                    if (address.length > 500) {
                        isValid = false;
                        errorMessage = 'Địa chỉ không được vượt quá 500 ký tự!';
                    }
                    
                    // Validate Birthdate (if provided)
                    if (birthdate) {
                        const birthDate = new Date(birthdate);
                        const today = new Date();
                        const minDate = new Date(today.getFullYear() - 120, today.getMonth(), today.getDate());
                        const maxDate = new Date(today.getFullYear() - 13, today.getMonth(), today.getDate());
                        
                        if (birthDate > today) {
                            isValid = false;
                            errorMessage = 'Ngày sinh không được là tương lai!';
                        } else if (birthDate < minDate) {
                            isValid = false;
                            errorMessage = 'Ngày sinh không hợp lệ!';
                        } else if (birthDate > maxDate) {
                            isValid = false;
                            errorMessage = 'Bạn phải ít nhất 13 tuổi!';
                        }
                    }
                    
                    if (!isValid) {
                        event.preventDefault();
                        event.stopPropagation();
                        alert(errorMessage);
                        return false;
                    }
                    
                    form.classList.add('was-validated');
                }, false);
            })();
            
            // Real-time validation feedback
            document.querySelectorAll('#profileForm input, #profileForm textarea').forEach(function(input) {
                input.addEventListener('blur', function() {
                    if (this.checkValidity()) {
                        this.classList.remove('is-invalid');
                        this.classList.add('is-valid');
                    } else {
                        this.classList.remove('is-valid');
                        this.classList.add('is-invalid');
                    }
                });
            });
        });
        </c:if>
    </script>
</body>
</html>

