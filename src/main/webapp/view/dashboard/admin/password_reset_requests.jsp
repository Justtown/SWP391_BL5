<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Yêu Cầu Đặt Lại Mật Khẩu - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        body {
            background-color: #f8f9fa;
            padding: 20px;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        .requests-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 30px;
            max-width: 100%;
        }
        .page-title {
            font-size: 1.75rem;
            font-weight: 600;
            margin-bottom: 25px;
            color: #212529;
        }
        .alert {
            border-radius: 6px;
            margin-bottom: 20px;
            border: none;
        }
        .filter-section {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 6px;
            margin-bottom: 25px;
            border: 1px solid #e9ecef;
        }
        .filter-section label {
            font-weight: 500;
            color: #495057;
            margin-bottom: 8px;
            font-size: 0.9rem;
        }
        .table-container {
            overflow-x: auto;
            border: 1px solid #dee2e6;
            border-radius: 6px;
        }
        .table {
            margin-bottom: 0;
        }
        .table thead th {
            background-color: #f8f9fa;
            color: #495057;
            border-bottom: 2px solid #dee2e6;
            font-weight: 600;
            font-size: 0.9rem;
            padding: 12px 15px;
        }
        .table tbody td {
            padding: 12px 15px;
            vertical-align: middle;
        }
        .table tbody tr:hover {
            background-color: #f8f9fa;
        }
        .btn-approve {
            background-color: #28a745;
            border-color: #28a745;
            color: white;
            font-size: 0.875rem;
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
            font-size: 0.875rem;
        }
        .btn-reject:hover {
            background-color: #c82333;
            border-color: #bd2130;
            color: white;
        }
        .empty-state {
            text-align: center;
            padding: 60px 20px;
            color: #6c757d;
        }
        .empty-state h3 {
            font-size: 1.25rem;
            font-weight: 500;
            margin-top: 15px;
        }
        .badge-pending {
            background-color: #fff3cd;
            color: #856404;
            padding: 4px 10px;
            border-radius: 4px;
            font-weight: 500;
            font-size: 0.85rem;
        }
        .badge-approved {
            background-color: #d4edda;
            color: #155724;
            padding: 4px 10px;
            border-radius: 4px;
            font-weight: 500;
            font-size: 0.85rem;
        }
        .badge-rejected {
            background-color: #f8d7da;
            color: #721c24;
            padding: 4px 10px;
            border-radius: 4px;
            font-weight: 500;
            font-size: 0.85rem;
        }
        .timestamp {
            font-size: 0.875rem;
            color: #6c757d;
        }
        .pagination {
            margin-top: 25px;
        }
        .page-link {
            color: #495057;
        }
        .page-item.active .page-link {
            background-color: #0d6efd;
            border-color: #0d6efd;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="requests-container">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="page-title mb-0">Quản Lý Yêu Cầu Đặt Lại Mật Khẩu</h1>
                <a href="${pageContext.request.contextPath}/manage-account" class="btn btn-outline-secondary btn-sm">
                    Quay lại
                </a>
            </div>
            
            <%-- Display messages --%>
            <c:if test="${not empty message}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>
            
            <%-- Search and Filter Section --%>
            <div class="filter-section">
                <form method="GET" action="${pageContext.request.contextPath}/admin/password-reset-requests">
                    <div class="row g-3">
                        <div class="col-md-6">
                            <label for="keyword" class="form-label">Tìm kiếm</label>
                            <input type="text" class="form-control" id="keyword" name="keyword" 
                                   placeholder="Email hoặc User ID" 
                                   value="${keyword}">
                        </div>
                        <div class="col-md-4">
                            <label for="status" class="form-label">Lọc theo trạng thái</label>
                            <select class="form-select" id="status" name="status">
                                <option value="all" ${statusFilter == 'all' ? 'selected' : ''}>Tất cả</option>
                                <option value="pending" ${statusFilter == 'pending' ? 'selected' : ''}>Đang chờ</option>
                                <option value="approved" ${statusFilter == 'approved' ? 'selected' : ''}>Đã duyệt</option>
                                <option value="rejected" ${statusFilter == 'rejected' ? 'selected' : ''}>Đã từ chối</option>
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">Lọc</button>
                        </div>
                    </div>
                </form>
            </div>
            
            <%-- Statistics --%>
            <div class="mb-3">
                <p class="text-muted mb-0">
                    Tổng số yêu cầu: <strong>${totalRecords}</strong>
                </p>
            </div>
            
            <c:choose>
                <c:when test="${empty requests || requests.size() == 0}">
                    <div class="empty-state">
                        <h3>Không có yêu cầu nào</h3>
                        <p class="text-muted">Không tìm thấy yêu cầu nào phù hợp với bộ lọc của bạn.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-container">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Email</th>
                                    <th>User ID</th>
                                    <th>Thời gian yêu cầu</th>
                                    <th>Trạng thái</th>
                                    <th>Đã đổi mật khẩu</th>
                                    <th>Hành động</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="request" items="${requests}">
                                    <tr>
                                        <td><strong>#${request.id}</strong></td>
                                        <td>${request.email}</td>
                                        <td>${request.userId}</td>
                                        <td class="timestamp">
                                            <fmt:formatDate value="${request.requestTime}" pattern="dd/MM/yyyy HH:mm" />
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${request.status == 'pending'}">
                                                    <span class="badge-pending">Đang chờ</span>
                                                </c:when>
                                                <c:when test="${request.status == 'approved'}">
                                                    <span class="badge-approved">Đã duyệt</span>
                                                </c:when>
                                                <c:when test="${request.status == 'rejected'}">
                                                    <span class="badge-rejected">Đã từ chối</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge-pending">${request.status}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:if test="${request.status == 'approved'}">
                                                <c:choose>
                                                    <c:when test="${request.passwordChanged}">
                                                        <span class="badge bg-success">Đã đổi</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-warning text-dark">Chưa đổi</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:if>
                                            <c:if test="${request.status != 'approved'}">
                                                <span class="text-muted">-</span>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${request.status == 'pending'}">
                                                <form action="${pageContext.request.contextPath}/admin/password-reset-requests" 
                                                      method="post" style="display: inline-block; margin-right: 5px;">
                                                    <input type="hidden" name="action" value="approve">
                                                    <input type="hidden" name="requestId" value="${request.id}">
                                                    <button type="submit" class="btn btn-approve btn-sm" 
                                                            onclick="return confirm('Phê duyệt yêu cầu này? Mật khẩu mới sẽ được gửi qua email đến: ${request.email}');">
                                                        Phê duyệt
                                                    </button>
                                                </form>
                                                <form action="${pageContext.request.contextPath}/admin/password-reset-requests" 
                                                      method="post" style="display: inline-block;">
                                                    <input type="hidden" name="action" value="reject">
                                                    <input type="hidden" name="requestId" value="${request.id}">
                                                    <button type="submit" class="btn btn-reject btn-sm"
                                                            onclick="return confirm('Từ chối yêu cầu này?');">
                                                        Từ chối
                                                    </button>
                                                </form>
                                            </c:if>
                                            <c:if test="${request.status != 'pending'}">
                                                <span class="text-muted">-</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                    
                    <%-- Pagination --%>
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?keyword=${keyword}&status=${statusFilter}&page=${currentPage - 1}">Trước</a>
                                </li>
                                
                                <c:forEach var="i" begin="1" end="${totalPages}">
                                    <c:if test="${i == 1 || i == totalPages || (i >= currentPage - 2 && i <= currentPage + 2)}">
                                        <li class="page-item ${i == currentPage ? 'active' : ''}">
                                            <a class="page-link" href="?keyword=${keyword}&status=${statusFilter}&page=${i}">${i}</a>
                                        </li>
                                    </c:if>
                                    <c:if test="${i == currentPage - 3 || i == currentPage + 3}">
                                        <li class="page-item disabled">
                                            <span class="page-link">...</span>
                                        </li>
                                    </c:if>
                                </c:forEach>
                                
                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?keyword=${keyword}&status=${statusFilter}&page=${currentPage + 1}">Sau</a>
                                </li>
                            </ul>
                        </nav>
                        <div class="text-center mt-2">
                            <small class="text-muted">
                                Trang ${currentPage} / ${totalPages}
                            </small>
                        </div>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

