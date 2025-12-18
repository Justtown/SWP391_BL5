<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/17/2025
  Time: 10:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh sách máy (Customer)</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
</head>

<body>

<jsp:include page="/view/common/dashboard/sideBar.jsp"/>

<div class="main-content">

    <div class="d-flex justify-content-between align-items-center mb-4">
        <h4 class="mb-0">
            <i class="fas fa-tractor me-2"></i>
            Danh sách máy
        </h4>

        <a href="${ctx}/customer/dashboard"
           class="btn btn-outline-secondary">
            <i class="fas fa-arrow-left me-1"></i>
            Quay lại
        </a>
    </div>


    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Mã máy</th>
                    <th>Tên máy</th>
                    <th>Loại máy</th>
                    <th>Trạng thái</th>
                    <th class="text-center">Chi tiết</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="m" items="${machines}">
                    <tr>
                        <td>#${m.id}</td>
                        <td><strong>${m.machineCode}</strong></td>
                        <td>${m.machineName}</td>
                        <td>${m.machineTypeName}</td>

                        <td>
                            <c:choose>
                                <c:when test="${m.status == 'ACTIVE'}">
                                    <span class="badge bg-success">ACTIVE</span>
                                </c:when>
                                <c:when test="${m.status == 'INACTIVE'}">
                                    <span class="badge bg-secondary">INACTIVE</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">DISCONTINUED</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td class="text-center">
                            <a href="${ctx}/customer/machines?action=detail&id=${m.id}"
                               class="btn btn-sm btn-outline-primary">
                                <i class="fas fa-eye"></i> Xem
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty machines}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">
                            Không có máy nào
                        </td>
                    </tr>
                </c:if>

                </tbody>
            </table>
        </div>
    </div>

</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
