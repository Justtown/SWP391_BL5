<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Duyệt Đơn hàng - Argo Machine Management</title>
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

        .pending-row {
            background-color: #fff3cd;
        }

        .pending-badge {
            position: relative;
        }

        .pending-badge::after {
            content: '';
            position: absolute;
            top: -2px;
            right: -2px;
            width: 8px;
            height: 8px;
            background: #dc3545;
            border-radius: 50%;
            animation: pulse 1.5s infinite;
        }

        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.5; }
            100% { opacity: 1; }
        }

        .order-code {
            font-weight: 600;
            color: #0d6efd;
        }

        .stats-card {
            background: linear-gradient(135deg, #ffc107 0%, #ff9800 100%);
            color: white;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 1rem;
        }

        .stats-number {
            font-size: 2rem;
            font-weight: bold;
        }

        .availability-badge {
            font-size: 0.7rem;
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
                <h4 class="mb-1"><i class="fas fa-clipboard-check me-2"></i>Duyệt Đơn hàng</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Duyệt đơn hàng</li>
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

        <!-- Content -->
        <div class="container-fluid">
            <!-- Pending Orders Stats -->
            <c:if test="${pendingCount > 0}">
                <div class="stats-card">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <div class="stats-number">${pendingCount}</div>
                            <div>đơn hàng đang chờ duyệt</div>
                        </div>
                        <i class="fas fa-clock fa-3x opacity-50"></i>
                    </div>
                </div>
            </c:if>

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
                <h5 class="mb-4">Danh sách đơn hàng</h5>

                <!-- Filter Section -->
                <form class="filter-section" method="get" action="${pageContext.request.contextPath}/manager/orders">
                    <select class="form-select filter-dropdown" name="status">
                        <option value="All" ${statusFilter == 'All' ? 'selected' : ''}>Tất cả trạng thái</option>
                        <option value="PENDING" ${statusFilter == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
                        <option value="CONVERTED" ${statusFilter == 'CONVERTED' ? 'selected' : ''}>Đã duyệt</option>
                        <option value="REJECTED" ${statusFilter == 'REJECTED' ? 'selected' : ''}>Đã từ chối</option>
                    </select>

                    <div class="search-container">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" class="form-control search-input" name="keyword"
                               placeholder="Tìm theo mã đơn, tên khách hàng, tên sale..." value="${keyword}">
                    </div>

                    <button type="submit" class="btn btn-outline-primary">
                        <i class="fas fa-filter me-1"></i> Lọc
                    </button>
                    <a href="${pageContext.request.contextPath}/manager/orders" class="btn btn-outline-secondary">
                        <i class="fas fa-redo me-1"></i> Reset
                    </a>
                </form>

                <!-- Orders Table -->
                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light">
                            <tr>
                                <th style="width: 50px">#</th>
                                <th>Mã đơn</th>
                                <th>Khách hàng</th>
                                <th>Sale</th>
                                <th>Ngày thuê</th>
                                <th>Số máy</th>
                                <th>Trạng thái</th>
                                <th>Ngày tạo</th>
                                <th style="width: 180px">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty orders}">
                                    <tr>
                                        <td colspan="9" class="text-center py-4 text-muted">
                                            <i class="fas fa-inbox fa-2x mb-2 d-block"></i>
                                            Không có đơn hàng nào
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${orders}" var="order" varStatus="loop">
                                        <tr class="${order.status == 'PENDING' ? 'pending-row' : ''}">
                                            <td>${(currentPage - 1) * 10 + loop.index + 1}</td>
                                            <td>
                                                <span class="order-code">${order.orderCode}</span>
                                            </td>
                                            <td>
                                                <div>${order.customerName}</div>
                                                <small class="text-muted">${order.customerPhone}</small>
                                            </td>
                                            <td>
                                                <small>${order.saleName}</small>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${order.startDate}" pattern="dd/MM/yyyy"/>
                                                <c:if test="${not empty order.endDate}">
                                                    <br><small class="text-muted">- <fmt:formatDate value="${order.endDate}" pattern="dd/MM/yyyy"/></small>
                                                </c:if>
                                            </td>
                                            <td>
                                                <span class="badge bg-secondary">${order.items.size()} máy</span>
                                            </td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.status == 'PENDING'}">
                                                        <span class="badge bg-warning text-dark status-badge pending-badge">
                                                            <i class="fas fa-clock me-1"></i>Chờ duyệt
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.status == 'CONVERTED'}">
                                                        <span class="badge bg-success status-badge">
                                                            <i class="fas fa-check me-1"></i>Đã duyệt
                                                        </span>
                                                        <c:if test="${not empty order.contractCode}">
                                                            <br><small class="text-success">${order.contractCode}</small>
                                                        </c:if>
                                                    </c:when>
                                                    <c:when test="${order.status == 'REJECTED'}">
                                                        <span class="badge bg-danger status-badge">
                                                            <i class="fas fa-times me-1"></i>Từ chối
                                                        </span>
                                                    </c:when>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            </td>
                                            <td>
                                                <div class="action-btn-group">
                                                    <button type="button" class="btn btn-info btn-sm"
                                                            onclick="viewDetail(${order.id})" title="Xem chi tiết">
                                                        <i class="fas fa-eye"></i>
                                                    </button>
                                                    <c:if test="${order.status == 'PENDING'}">
                                                        <button type="button" class="btn btn-success btn-sm"
                                                                onclick="confirmApprove(${order.id}, '${order.orderCode}')"
                                                                title="Duyệt">
                                                            <i class="fas fa-check"></i>
                                                        </button>
                                                        <button type="button" class="btn btn-danger btn-sm"
                                                                onclick="showRejectModal(${order.id}, '${order.orderCode}')"
                                                                title="Từ chối">
                                                            <i class="fas fa-times"></i>
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
                            <a href="${pageContext.request.contextPath}/manager/orders?page=${currentPage - 1}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn">
                                <i class="fas fa-chevron-left"></i>
                            </a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="i">
                            <a href="${pageContext.request.contextPath}/manager/orders?page=${i}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn ${currentPage == i ? 'active' : ''}">${i}</a>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/manager/orders?page=${currentPage + 1}&status=${statusFilter}&keyword=${keyword}"
                               class="pagination-btn">
                                <i class="fas fa-chevron-right"></i>
                            </a>
                        </c:if>
                    </div>
                </c:if>

                <div class="text-muted mt-3 text-center">
                    Tổng: ${totalOrders} đơn hàng
                </div>
            </div>
        </div>
    </div>

    <!-- Detail Modal -->
    <div class="modal fade" id="detailModal" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-clipboard-list me-2"></i>Chi tiết đơn hàng</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body" id="detailContent">
                    <div class="text-center py-4">
                        <div class="spinner-border text-primary" role="status"></div>
                        <p class="mt-2">Đang tải...</p>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                </div>
            </div>
        </div>
    </div>

    <!-- Approve Confirm Modal -->
    <div class="modal fade" id="approveModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-success text-white">
                    <h5 class="modal-title"><i class="fas fa-check-circle me-2"></i>Xác nhận duyệt</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/manager/orders">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="approve">
                        <input type="hidden" name="id" id="approveOrderId">
                        <p>Bạn có chắc muốn duyệt đơn hàng <strong id="approveOrderCode"></strong>?</p>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle me-2"></i>
                            Sau khi duyệt, hệ thống sẽ tự động:
                            <ul class="mb-0 mt-2">
                                <li>Tạo hợp đồng mới</li>
                                <li>Chuyển các máy sang trạng thái "Đang cho thuê"</li>
                            </ul>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-success">
                            <i class="fas fa-check me-1"></i>Duyệt đơn
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Reject Modal -->
    <div class="modal fade" id="rejectModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title"><i class="fas fa-times-circle me-2"></i>Từ chối đơn hàng</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/manager/orders">
                    <div class="modal-body">
                        <input type="hidden" name="action" value="reject">
                        <input type="hidden" name="id" id="rejectOrderId">
                        <p>Từ chối đơn hàng <strong id="rejectOrderCode"></strong></p>
                        <div class="mb-3">
                            <label class="form-label">Lý do từ chối <span class="text-danger">*</span></label>
                            <textarea class="form-control" name="rejectReason" rows="3" required
                                      placeholder="Nhập lý do từ chối..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-danger">
                            <i class="fas fa-times me-1"></i>Từ chối
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Logout Modal -->
    <div class="modal fade" id="logoutModal" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Xác nhận đăng xuất</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p>Bạn có chắc chắn muốn đăng xuất?</p>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <a href="${pageContext.request.contextPath}/authen/logout" class="btn btn-danger">Đăng xuất</a>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function viewDetail(id) {
            const modal = new bootstrap.Modal(document.getElementById('detailModal'));
            modal.show();

            fetch('${pageContext.request.contextPath}/manager/orders?action=detail&id=' + id)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        const order = data.order;
                        const itemsWithAvailability = data.itemsWithAvailability || [];

                        let itemsHtml = '';
                        if (itemsWithAvailability.length > 0) {
                            itemsHtml = '<table class="table table-sm"><thead><tr><th>Serial</th><th>Dòng máy</th><th>Giá</th><th>Tình trạng</th></tr></thead><tbody>';
                            itemsWithAvailability.forEach(itemData => {
                                const item = itemData.item;
                                const isAvailable = itemData.isAvailable;
                                const currentStatus = itemData.currentStatus;
                                const currentRentalStatus = itemData.currentRentalStatus;

                                let availabilityBadge = isAvailable
                                    ? '<span class="badge bg-success availability-badge">Sẵn sàng</span>'
                                    : '<span class="badge bg-danger availability-badge">' + currentStatus + '/' + currentRentalStatus + '</span>';

                                itemsHtml += '<tr><td>' + (item.serialNumber || '-') + '</td>';
                                itemsHtml += '<td>' + (item.modelName || '-') + ' (' + (item.brand || '-') + ')</td>';
                                itemsHtml += '<td>' + (item.price ? new Intl.NumberFormat('vi-VN').format(item.price) + ' VNĐ' : '-') + '</td>';
                                itemsHtml += '<td>' + availabilityBadge + '</td></tr>';
                            });
                            itemsHtml += '</tbody></table>';
                        } else {
                            itemsHtml = '<p class="text-muted">Không có máy nào</p>';
                        }

                        let statusBadge = '';
                        if (order.status === 'PENDING') {
                            statusBadge = '<span class="badge bg-warning text-dark">Chờ duyệt</span>';
                        } else if (order.status === 'CONVERTED') {
                            statusBadge = '<span class="badge bg-success">Đã duyệt</span>';
                        } else if (order.status === 'REJECTED') {
                            statusBadge = '<span class="badge bg-danger">Từ chối</span>';
                        }

                        document.getElementById('detailContent').innerHTML =
                            '<div class="row mb-3">' +
                            '<div class="col-md-6"><strong>Mã đơn:</strong> ' + order.orderCode + '</div>' +
                            '<div class="col-md-6"><strong>Trạng thái:</strong> ' + statusBadge + '</div>' +
                            '</div>' +
                            '<div class="row mb-3">' +
                            '<div class="col-md-6"><strong>Khách hàng:</strong> ' + order.customerName + '</div>' +
                            '<div class="col-md-6"><strong>SĐT:</strong> ' + (order.customerPhone || '-') + '</div>' +
                            '</div>' +
                            '<div class="row mb-3">' +
                            '<div class="col-md-6"><strong>Sale:</strong> ' + order.saleName + '</div>' +
                            '<div class="col-md-6"><strong>Manager:</strong> ' + (order.managerName || '-') + '</div>' +
                            '</div>' +
                            '<div class="row mb-3">' +
                            '<div class="col-md-6"><strong>Ngày bắt đầu:</strong> ' + order.startDate + '</div>' +
                            '<div class="col-md-6"><strong>Ngày kết thúc:</strong> ' + (order.endDate || '-') + '</div>' +
                            '</div>' +
                            (order.rejectReason ? '<div class="alert alert-danger"><strong>Lý do từ chối:</strong> ' + order.rejectReason + '</div>' : '') +
                            (order.contractCode ? '<div class="alert alert-success"><strong>Hợp đồng:</strong> ' + order.contractCode + '</div>' : '') +
                            '<div class="mb-3"><strong>Ghi chú:</strong> ' + (order.note || 'Không có') + '</div>' +
                            '<hr><h6>Danh sách máy:</h6>' + itemsHtml;
                    } else {
                        document.getElementById('detailContent').innerHTML =
                            '<div class="alert alert-danger">' + data.message + '</div>';
                    }
                })
                .catch(error => {
                    document.getElementById('detailContent').innerHTML =
                        '<div class="alert alert-danger">Có lỗi xảy ra khi tải dữ liệu</div>';
                });
        }

        function confirmApprove(id, code) {
            document.getElementById('approveOrderId').value = id;
            document.getElementById('approveOrderCode').textContent = code;
            const modal = new bootstrap.Modal(document.getElementById('approveModal'));
            modal.show();
        }

        function showRejectModal(id, code) {
            document.getElementById('rejectOrderId').value = id;
            document.getElementById('rejectOrderCode').textContent = code;
            const modal = new bootstrap.Modal(document.getElementById('rejectModal'));
            modal.show();
        }
    </script>
</body>
</html>
