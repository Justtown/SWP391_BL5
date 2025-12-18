<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/14/2025
  Time: 9:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="ctx" value="${pageContext.request.contextPath}"/>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Request Management</title>

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
            <i class="fas fa-clipboard-list me-2"></i>
            <c:choose>
                <c:when test="${sessionScope.roleName == 'sale'}">
                    Quản lý Request
                </c:when>
                <c:otherwise>
                    Request của tôi
                </c:otherwise>
            </c:choose>
        </h4>

        <!-- CUSTOMER: CREATE REQUEST -->
        <c:if test="${sessionScope.roleName == 'customer'}">
            <a href="${ctx}/requests?action=create" class="btn btn-primary">
                <i class="fas fa-plus me-1"></i> Tạo Request
            </a>
        </c:if>
    </div>


    <!-- REQUEST TABLE -->
    <div class="card shadow-sm">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                <tr>
                    <th>ID</th>
                    <th>Tiêu đề</th>
                    <th>Mô tả</th>
                    <th>Trạng thái</th>
                    <th>Ngày tạo</th>

                    <c:if test="${sessionScope.roleName == 'customer'}">
                        <th>Feedback</th>
                    </c:if>

                    <c:if test="${sessionScope.roleName == 'sale'}">
                        <th>Customer</th>
                        <th class="text-center">Thao tác</th>
                    </c:if>
                </tr>
                </thead>


                <tbody>
                <c:forEach var="r" items="${requests}">
                    <tr>
                        <td>#${r.id}</td>

                        <td>
                            <strong>${r.title}</strong>
                        </td>

                        <!-- DESCRIPTION -->
                        <td>
            <span class="text-muted">
                    ${r.description}
            </span>
                        </td>

                        <!-- STATUS -->
                        <td>
                            <c:choose>
                                <c:when test="${r.status == 'PENDING'}">
                                    <span class="badge bg-warning text-dark">PENDING</span>
                                </c:when>
                                <c:when test="${r.status == 'APPROVED'}">
                                    <span class="badge bg-success">APPROVED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger">DECLINED</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>${r.createdAt}</td>

                        <!-- CUSTOMER FEEDBACK -->
                        <c:if test="${sessionScope.roleName == 'customer'}">
                            <td>
                                <c:choose>
                                    <c:when test="${not empty r.feedback}">
                        <span class="text-success">
                            <i class="fas fa-comment-dots me-1"></i>
                            ${r.feedback}
                        </span>
                                    </c:when>
                                    <c:otherwise>
                        <span class="text-muted fst-italic">
                            Chưa có phản hồi
                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </c:if>

                        <!-- SALE ONLY -->
                        <c:if test="${sessionScope.roleName == 'sale'}">
                            <td>${r.customerUsername}</td>
                            <td class="text-center">
                                <a href="${ctx}/requests?action=edit&id=${r.id}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="fas fa-pen"></i> Xử lý
                                </a>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>

                </tbody>
            </table>
        </div>
    </div>

</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

