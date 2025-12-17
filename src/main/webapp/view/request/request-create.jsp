<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/14/2025
  Time: 9:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tạo Request</title>

    <!-- Bootstrap & FontAwesome -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
</head>

<body>

<!-- SIDEBAR -->
<jsp:include page="/view/common/dashboard/sideBar.jsp"/>

<!-- MAIN CONTENT -->
<div class="main-content">

    <!-- PAGE HEADER -->
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="mb-0">
            <i class="fas fa-plus-circle me-2"></i>
            Tạo Request mới
        </h4>

        <a href="${ctx}/requests" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Quay lại
        </a>
    </div>

    <!-- CREATE REQUEST FORM -->
    <c:if test="${sessionScope.roleName == 'customer'}">
        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="fas fa-clipboard-list me-2"></i>
                    Thông tin Request
                </h5>
            </div>

            <div class="card-body">
                <form method="post" action="${ctx}/requests">
                    <input type="hidden" name="action" value="create"/>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Tiêu đề <span class="text-danger">*</span>
                        </label>
                        <input type="text"
                               name="title"
                               class="form-control"
                               placeholder="Nhập tiêu đề request"
                               required>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-semibold">
                            Mô tả chi tiết <span class="text-danger">*</span>
                        </label>
                        <textarea name="description"
                                  rows="5"
                                  class="form-control"
                                  placeholder="Mô tả chi tiết yêu cầu của bạn"
                                  required></textarea>
                    </div>

                    <div class="d-flex justify-content-end gap-2">
                        <a href="${ctx}/requests" class="btn btn-outline-secondary">
                            Hủy
                        </a>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-paper-plane me-1"></i>
                            Gửi Request
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </c:if>

    <!-- BLOCK TRÁI QUYỀN -->
    <c:if test="${sessionScope.roleName != 'customer'}">
        <div class="alert alert-danger">
            <i class="fas fa-ban me-1"></i>
            Bạn không có quyền tạo request.
        </div>
    </c:if>

</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

