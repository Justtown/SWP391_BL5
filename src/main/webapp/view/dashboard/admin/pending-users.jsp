<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Chờ Duyệt - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f5f5f5;
        }
        .main-content {
            margin-left: 250px;
            padding: 20px;
            min-height: 100vh;
        }
        .pending-users-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
        .page-title {
            font-size: 1.8rem;
            font-weight: bold;
            margin-bottom: 20px;
        }
        .badge-pending {
            background-color: #ffc107;
            color: #212529;
        }
        .btn-approve {
            background-color: #28a745;
            border-color: #28a745;
            color: white;
        }
        .btn-approve:hover {
            background-color: #218838;
            border-color: #1e7e34;
            color: white;
        }
        .btn-reject {
            background-color: #dc3545;
            border-color: #dc3545;
            color: white;
        }
        .btn-reject:hover {
            background-color: #c82333;
            border-color: #bd2130;
            color: white;
        }
        .table thead th {
            background-color: #f8f9fa;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
        }
        .no-data-message {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        .stats-badge {
            font-size: 0.9rem;
            padding: 8px 15px;
        }
        .role-badge {
            text-transform: capitalize;
        }
        @media (max-width: 768px) {
            .main-content {
                margin-left: 0;
            }
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <div class="pending-users-container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="page-title mb-0">
                    <i class="fas fa-user-clock me-2"></i>User Chờ Duyệt
                </h1>
                <span class="badge bg-warning text-dark stats-badge">
                    <i class="fas fa-hourglass-half me-1"></i>
                    ${totalPending} user đang chờ
                </span>
            </div>

            <!-- Success Message -->
            <c:if test="${not empty successMessage}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>${successMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Error Message -->
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${errorMessage}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Pending Users Table -->
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>STT</th>
                            <th>Username</th>
                            <th>Họ tên</th>
                            <th>Email</th>
                            <th>Vai trò yêu cầu</th>
                            <th>Ngày đăng ký</th>
                            <th>Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty pendingUsers && pendingUsers.size() > 0}">
                                <c:forEach var="user" items="${pendingUsers}" varStatus="loop">
                                    <tr>
                                        <td>${loop.index + 1}</td>
                                        <td><strong>${user.username}</strong></td>
                                        <td>${user.fullName}</td>
                                        <td>${user.email}</td>
                                        <td>
                                            <span class="badge bg-info role-badge">
                                                <c:choose>
                                                    <c:when test="${user.roleName != null}">${user.roleName}</c:when>
                                                    <c:otherwise>N/A</c:otherwise>
                                                </c:choose>
                                            </span>
                                        </td>
                                        <td>
                                            <fmt:formatDate value="${user.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/admin/pending-users"
                                                  method="POST" style="display: inline;">
                                                <input type="hidden" name="userId" value="${user.id}">
                                                <input type="hidden" name="action" value="approve">
                                                <button type="submit" class="btn btn-approve btn-sm"
                                                        onclick="return confirm('Phê duyệt user ${user.fullName}?')">
                                                    <i class="fas fa-check"></i> Phê duyệt
                                                </button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/admin/pending-users"
                                                  method="POST" style="display: inline;">
                                                <input type="hidden" name="userId" value="${user.id}">
                                                <input type="hidden" name="action" value="reject">
                                                <button type="submit" class="btn btn-reject btn-sm"
                                                        onclick="return confirm('Từ chối user ${user.fullName}?')">
                                                    <i class="fas fa-times"></i> Từ chối
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="7" class="no-data-message">
                                        <i class="fas fa-inbox fa-3x mb-3 text-muted d-block"></i>
                                        <p class="mb-0">Không có user nào đang chờ duyệt</p>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Include Footer -->
    <jsp:include page="/view/common/dashboard/footer.jsp" />

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
