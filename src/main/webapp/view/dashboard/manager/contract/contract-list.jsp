<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Hợp đồng - Argo Machine Management</title>
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

        .filter-section {
            display: flex;
            gap: 10px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            align-items: center;
        }

        .filter-dropdown {
            width: 150px;
            flex-shrink: 0;
        }

        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 200px;
        }

        .search-input {
            padding-left: 40px;
        }

        .search-icon {
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }

        .pagination-container {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-top: 20px;
            gap: 10px;
        }

        .pagination-btn {
            padding: 8px 16px;
            border: 1px solid #dee2e6;
            background: white;
            color: #495057;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            transition: all 0.3s;
        }

        .pagination-btn:hover:not(.disabled) {
            background-color: #e9ecef;
            border-color: #adb5bd;
        }

        .pagination-btn.disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .pagination-btn.active {
            background-color: #0d6efd;
            color: white;
            border-color: #0d6efd;
        }

        .status-badge {
            font-size: 0.8rem;
        }

        .action-btn-group .btn {
            padding: 0.25rem 0.5rem;
            font-size: 0.75rem;
        }

        .contract-code {
            font-weight: 600;
            color: #0d6efd;
        }

        .stats-row {
            display: flex;
            gap: 1rem;
            margin-bottom: 1.5rem;
        }

        .stat-card {
            flex: 1;
            border-radius: 10px;
            padding: 1rem;
            color: white;
        }

        .stat-card.draft {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
        }

        .stat-card.active {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }

        .stat-card.finished {
            background: linear-gradient(135deg, #17a2b8 0%, #6f42c1 100%);
        }

        .stat-number {
            font-size: 1.8rem;
            font-weight: bold;
        }

        .draft-row {
            background-color: #f8f9fa;
        }

        .active-row {
            background-color: #d4edda;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <!-- Main Content -->
    <div class="main-content">
        <!-- Page Header -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-file-contract me-2"></i>Quản lý Hợp đồng</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Hợp đồng</li>
                    </ol>
                </nav>
            </div>
            <div class="d-flex align-items-center gap-2">
                <a href="${pageContext.request.contextPath}/manager/contracts?action=create" class="btn btn-primary">
                    <i class="fas fa-plus me-1"></i> Tạo hợp đồng mới
                </a>
                <span class="me-3">
                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
                </span>
            </div>
        </div>

        <!-- Content -->
        <div class="container-fluid">
            <!-- Stats Row -->
            <div class="stats-row">
                <div class="stat-card draft">
                    <div class="stat-number">${draftCount}</div>
                    <div>Nháp</div>
                </div>
                <div class="stat-card active">
                    <div class="stat-number">${activeCount}</div>
                    <div>Đang hoạt động</div>
                </div>
                <div class="stat-card finished">
                    <div class="stat-number">${finishedCount}</div>
                    <div>Hoàn thành</div>
                </div>
            </div>

            <!-- Alert Messages -->
            <c:if test="${not empty successMsg}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle me-2"></i>${successMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>${errorMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="content-card">
                <h5 class="mb-4">Danh sách hợp đồng</h5>

                <!-- Filter Section -->
                <form class="filter-section" method="get" action="${pageContext.request.contextPath}/manager/contracts">
                    <select class="form-select filter-dropdown" name="status">
                        <option value="All" ${statusFilter == 'All' ? 'selected' : ''}>Tất cả trạng thái</option>
                        <option value="DRAFT" ${statusFilter == 'DRAFT' ? 'selected' : ''}>Nháp</option>
                        <option value="ACTIVE" ${statusFilter == 'ACTIVE' ? 'selected' : ''}>Đang hoạt động</option>
                        <option value="FINISHED" ${statusFilter == 'FINISHED' ? 'selected' : ''}>Hoàn thành</option>
                        <option value="CANCELLED" ${statusFilter == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
                    </select>

                    <div class="search-container">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" class="form-control search-input" name="keyword"
                               placeholder="Tìm theo mã hợp đồng, tên khách hàng..." value="${keyword}">
                    </div>

                    <button type="submit" class="btn btn-outline-primary">
                        <i class="fas fa-filter me-1"></i> Lọc
                    </button>
                    <a href="${pageContext.request.contextPath}/manager/contracts" class="btn btn-outline-secondary">
                        <i class="fas fa-redo me-1"></i> Reset
                    </a>
                </form>

                <!-- Contracts Table -->
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 50px">#</th>
                                <th>Mã HĐ</th>
                                <th>Khách hàng</th>
                                <th>Manager</th>
                                <th>Thời hạn</th>
                                <th>Số máy</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th style="width: 200px">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty contracts}">
                                    <tr>
                                        <td colspan="9" class="text-center py-4 text-muted">
                                            <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                            Không có hợp đồng nào
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${contracts}" var="contract" varStatus="loop">
                                        <tr class="${contract.status == 'DRAFT' ? 'draft-row' : (contract.status == 'ACTIVE' ? 'active-row' : '')}">
                                            <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                            <td>
                                                <span class="contract-code">${contract.contractCode}</span>
                                            </td>
                                            <td>
                                                <div>${contract.customerName}</div>
                                            </td>
                                            <td>
                                                <small>${contract.managerName}</small>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/>
                                                <c:if test="${not empty contract.endDate}">
                                                    <br><small class="text-muted">- <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/></small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span class="badge bg-secondary">${contract.items.size()} máy</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${contract.status == 'DRAFT'}">
                                                        <span class="badge bg-secondary status-badge">
                                                            <i class="fas fa-edit me-1"></i>Nháp
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${contract.status == 'ACTIVE'}">
                                                        <span class="badge bg-success status-badge">
                                                            <i class="fas fa-check-circle me-1"></i>Đang hoạt động
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${contract.status == 'FINISHED'}">
                                                        <span class="badge bg-info status-badge">
                                                            <i class="fas fa-flag-checkered me-1"></i>Hoàn thành
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${contract.status == 'CANCELLED'}">
                                                        <span class="badge bg-danger status-badge">
                                                            <i class="fas fa-ban me-1"></i>Đã hủy
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <div class="action-btn-group">
                                                    <a href="${pageContext.request.contextPath}/manager/contracts?action=detail&id=${contract.id}"
                                                       class="btn btn-info btn-sm" title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </a>
                                                    <c:if test="${contract.status == 'DRAFT'}">
                                                        <a href="${pageContext.request.contextPath}/manager/contracts?action=edit&id=${contract.id}"
                                                           class="btn btn-warning btn-sm" title="Sửa">
                                                            <i class="fas fa-edit"></i>
                                                        </a>
                                                        <button type="button" class="btn btn-success btn-sm"
                                                                onclick="confirmActivate(${contract.id}, '${contract.contractCode}')"
                                                                title="Kích hoạt">
                                                            <i class="fas fa-play"></i>
                                                        </button>
                                                        <button type="button" class="btn btn-danger btn-sm"
                                                                onclick="confirmCancel(${contract.id}, '${contract.contractCode}')"
                                                                title="Hủy">
                                                            <i class="fas fa-ban"></i>
                                                        </button>
                                                    </c:if>
                                                    <c:if test="${contract.status == 'ACTIVE'}">
                                                        <button type="button" class="btn btn-primary btn-sm"
                                                                onclick="confirmFinish(${contract.id}, '${contract.contractCode}')"
                                                                title="Hoàn thành & Trả máy">
                                                            <i class="fas fa-flag-checkered"></i>
                                                        </button>
                                                    </c:if>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination-container">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/manager/contracts?page=${currentPage - 1}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/manager/contracts?page=${i}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/manager/contracts?page=${currentPage + 1}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>

                <div class="text-muted mt-3 text-center">
                    Tổng: ${totalContracts} hợp đồng
                </div>
            </div>
        </div>
    </div>

    <!-- Activate Confirm Modal -->
    <div class="modal fade" id="activateModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title"><i class="fas fa-play-circle me-2"></i>Kích hoạt hợp đồng</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/manager/contracts">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="activate">
                        <input type="hidden" name="id" id="activateContractId">
                        <p>Bạn có chắc muốn kích hoạt hợp đồng <strong id="activateContractCode"></strong>?</p>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Sau khi kích hoạt:
                            <ul class="mb-0 mt-2">
                                <li>Hợp đồng sẽ chuyển sang trạng thái "Đang hoạt động"</li>
                                <li>Các máy trong hợp đồng sẽ chuyển sang trạng thái "Đang cho thuê"</li>
                                <li>Không thể sửa hợp đồng sau khi kích hoạt</li>
                            </ul>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-play me-1"></i>Kích hoạt
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Finish Confirm Modal -->
    <div class="modal fade" id="finishModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title"><i class="fas fa-flag-checkered me-2"></i>Hoàn thành hợp đồng</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/manager/contracts">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="finish">
                        <input type="hidden" name="id" id="finishContractId">
                        <p>Bạn có chắc muốn hoàn thành hợp đồng <strong id="finishContractCode"></strong>?</p>
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            Sau khi hoàn thành:
                            <ul class="mb-0 mt-2">
                                <li>Hợp đồng sẽ chuyển sang trạng thái "Hoàn thành"</li>
                                <li>Tất cả các máy sẽ được trả về và chuyển sang "Sẵn sàng"</li>
                                <li>Thao tác này không thể hoàn tác</li>
                            </ul>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-flag-checkered me-1"></i>Hoàn thành & Trả máy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Cancel Confirm Modal -->
    <div class="modal fade" id="cancelModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title"><i class="fas fa-ban me-2"></i>Hủy hợp đồng</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/manager/contracts">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="cancel">
                        <input type="hidden" name="id" id="cancelContractId">
                        <p>Bạn có chắc muốn hủy hợp đồng <strong id="cancelContractCode"></strong>?</p>
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-circle me-2"></i>
                            Thao tác này không thể hoàn tác!
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Quay lại</button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-ban me-1"></i>Hủy hợp đồng
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Logout Modal (from sidebar) -->

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmActivate(id, code) {
            document.getElementById('activateContractId').value = id;
            document.getElementById('activateContractCode').textContent = code;
            const modal = new bootstrap.Modal(document.getElementById('activateModal'));
            modal.show();
        }

        function confirmFinish(id, code) {
            document.getElementById('finishContractId').value = id;
            document.getElementById('finishContractCode').textContent = code;
            const modal = new bootstrap.Modal(document.getElementById('finishModal'));
            modal.show();
        }

        function confirmCancel(id, code) {
            document.getElementById('cancelContractId').value = id;
            document.getElementById('cancelContractCode').textContent = code;
            const modal = new bootstrap.Modal(document.getElementById('cancelModal'));
            modal.show();
        }
    </script>
</body>
</html>
