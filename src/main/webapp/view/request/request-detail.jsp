<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/16/2025
  Time: 9:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Xử lý Request</title>

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
            <i class="fas fa-clipboard-check me-2"></i>
            Xử lý Request #${request.id}
        </h4>

        <a href="${ctx}/requests" class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i> Quay lại
        </a>
    </div>

    <!-- SALE ONLY -->
    <c:if test="${sessionScope.roleName == 'sale'}">

        <div class="card shadow-sm">
            <div class="card-header bg-primary text-white">
                <h5 class="mb-0">
                    <i class="fas fa-file-alt me-2"></i>
                    Thông tin Request
                </h5>
            </div>

            <div class="card-body">

                <!-- TITLE -->
                <div class="mb-3">
                    <label class="form-label fw-semibold">Tiêu đề</label>
                    <input type="text"
                           class="form-control"
                           value="${request.title}"
                           readonly>
                </div>

                <!-- DESCRIPTION -->
                <div class="mb-4">
                    <label class="form-label fw-semibold">Mô tả chi tiết</label>
                    <textarea class="form-control"
                              rows="4"
                              readonly>${request.description}</textarea>
                </div>

                <!-- ====== CHỈ THÊM PHẦN DƯỚI ====== -->

                <form method="post" action="${ctx}/requests">
                    <input type="hidden" name="action" value="review"/>
                    <input type="hidden" name="id" value="${request.id}"/>

                    <!-- STATUS -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">
                            Quyết định
                        </label>
                        <select name="status" class="form-select" required>
                            <option value="APPROVED">APPROVE</option>
                            <option value="DECLINED">DECLINE</option>
                        </select>
                    </div>

                    <!-- FEEDBACK -->
                    <div class="mb-4">
                        <label class="form-label fw-semibold">
                            Feedback cho khách hàng
                        </label>
                        <textarea name="feedback"
                                  class="form-control"
                                  rows="4"
                                  placeholder="Nhập phản hồi cho khách hàng..."></textarea>
                    </div>

                    <!-- ACTION -->
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${ctx}/requests" class="btn btn-outline-secondary">
                            Hủy
                        </a>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-save me-1"></i> Lưu xử lý
                        </button>
                    </div>
                </form>

            </div>
        </div>

    </c:if>

    <!-- BLOCK TRÁI QUYỀN -->
    <c:if test="${sessionScope.roleName != 'sale'}">
        <div class="alert alert-danger">
            <i class="fas fa-ban me-1"></i>
            Bạn không có quyền truy cập trang này.
        </div>
    </c:if>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

