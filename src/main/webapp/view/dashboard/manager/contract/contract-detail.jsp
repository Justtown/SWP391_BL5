<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi tiết Hợp đồng - ${contract.contractCode}</title>
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

        .detail-label {
            font-weight: 600;
            color: #6c757d;
            margin-bottom: 0.25rem;
        }

        .detail-value {
            font-size: 1.1rem;
            margin-bottom: 1rem;
        }

        .contract-code {
            font-size: 1.5rem;
            font-weight: bold;
            color: #0d6efd;
        }

        .status-badge {
            font-size: 1rem;
            padding: 0.5rem 1rem;
        }

        .items-table th {
            background-color: #f8f9fa;
        }

        .total-row {
            background-color: #e9ecef;
            font-weight: bold;
        }

        .action-buttons {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
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
                <h4 class="mb-1"><i class="fas fa-file-contract me-2"></i>Chi tiết Hợp đồng</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/contracts">Hợp đồng</a></li>
                        <li class="breadcrumb-item active">${contract.contractCode}</li>
                    </ol>
                </nav>
            </div>
            <div class="d-flex align-items-center gap-2">
                <a href="${pageContext.request.contextPath}/manager/contracts" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-1"></i> Quay lại
                </a>
            </div>
        </div>

        <!-- Content -->
        <div class="container-fluid">
            <!-- Contract Info -->
            <div class="content-card">
                <div class="d-flex justify-content-between align-items-start mb-4">
                    <div>
                        <span class="contract-code">${contract.contractCode}</span>
                        <div class="mt-2">
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
                        </div>
                    </div>
                    <div class="action-buttons">
                        <c:if test="${contract.status == 'DRAFT'}">
                            <a href="${pageContext.request.contextPath}/manager/contracts?action=edit&id=${contract.id}"
                               class="btn btn-warning">
                                <i class="fas fa-edit me-1"></i> Sửa
                            </a>
                            <form method="post" action="${pageContext.request.contextPath}/manager/contracts" class="d-inline">
                                <input type="hidden" name="action" value="activate">
                                <input type="hidden" name="id" value="${contract.id}">
                                <button type="submit" class="btn btn-success"
                                        onclick="return confirm('Bạn có chắc muốn kích hoạt hợp đồng này?');">
                                    <i class="fas fa-play me-1"></i> Kích hoạt
                                </button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/manager/contracts" class="d-inline">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="id" value="${contract.id}">
                                <button type="submit" class="btn btn-danger"
                                        onclick="return confirm('Bạn có chắc muốn hủy hợp đồng này?');">
                                    <i class="fas fa-ban me-1"></i> Hủy
                                </button>
                            </form>
                        </c:if>
                        <c:if test="${contract.status == 'ACTIVE'}">
                            <form method="post" action="${pageContext.request.contextPath}/manager/contracts" class="d-inline">
                                <input type="hidden" name="action" value="finish">
                                <input type="hidden" name="id" value="${contract.id}">
                                <button type="submit" class="btn btn-primary"
                                        onclick="return confirm('Bạn có chắc muốn hoàn thành hợp đồng và trả máy?');">
                                    <i class="fas fa-flag-checkered me-1"></i> Hoàn thành & Trả máy
                                </button>
                            </form>
                        </c:if>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-label">Khách hàng</div>
                        <div class="detail-value">${contract.customerName}</div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-label">Manager phụ trách</div>
                        <div class="detail-value">${contract.managerName}</div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-label">Ngày bắt đầu</div>
                        <div class="detail-value">
                            <fmt:formatDate value="${contract.startDate}" pattern="dd/MM/yyyy"/>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-label">Ngày kết thúc</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${not empty contract.endDate}">
                                    <fmt:formatDate value="${contract.endDate}" pattern="dd/MM/yyyy"/>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">Chưa xác định</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6">
                        <div class="detail-label">Ngày tạo</div>
                        <div class="detail-value">
                            <fmt:formatDate value="${contract.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="detail-label">Cập nhật lần cuối</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${not empty contract.updatedAt}">
                                    <fmt:formatDate value="${contract.updatedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">-</span>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty contract.note}">
                    <div class="row">
                        <div class="col-12">
                            <div class="detail-label">Ghi chú</div>
                            <div class="detail-value">${contract.note}</div>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Contract Items -->
            <div class="content-card">
                <h5 class="mb-4"><i class="fas fa-cogs me-2"></i>Danh sách máy thuê</h5>

                <div class="table-responsive">
                    <table class="table table-hover items-table">
                        <thead>
                            <tr>
                                <th style="width: 50px">#</th>
                                <th>Serial Number</th>
                                <th>Mã Model</th>
                                <th>Tên Model</th>
                                <th>Hãng</th>
                                <th>Loại máy</th>
                                <th>Trạng thái</th>
                                <th class="text-end">Giá thuê</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty contract.items}">
                                    <tr>
                                        <td colspan="8" class="text-center py-4 text-muted">
                                            Không có máy nào trong hợp đồng
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach items="${contract.items}" var="item" varStatus="loop">
                                        <tr>
                                            <td>${loop.index + 1}</td>
                                            <td><strong>${item.serialNumber}</strong></td>
                                            <td>${item.modelCode}</td>
                                            <td>${item.modelName}</td>
                                            <td>${item.brand}</td>
                                            <td>${item.typeName}</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${item.rentalStatus == 'RENTED'}">
                                                        <span class="badge bg-warning text-dark">Đang cho thuê</span>
                                                    </c:when>
                                                    <c:when test="${item.rentalStatus == 'AVAILABLE'}">
                                                        <span class="badge bg-success">Sẵn sàng</span>
                                                    </c:when>
                                                    <c:when test="${item.rentalStatus == 'MAINTENANCE'}">
                                                        <span class="badge bg-secondary">Bảo trì</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-info">${item.rentalStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td class="text-end">
                                                <c:if test="${not empty item.price}">
                                                    <fmt:formatNumber value="${item.price}" type="number" groupingUsed="true"/> VND
                                                </c:if>
                                                <c:if test="${empty item.price}">
                                                    <span class="text-muted">-</span>
                                                </c:if>
                                            </td>
                                        </tr>
                                        <c:if test="${not empty item.note}">
                                            <tr>
                                                <td></td>
                                                <td colspan="7" class="text-muted small">
                                                    <i class="fas fa-sticky-note me-1"></i> ${item.note}
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                        <tfoot>
                            <tr class="total-row">
                                <td colspan="7" class="text-end">Tổng giá trị:</td>
                                <td class="text-end">
                                    <fmt:formatNumber value="${totalPrice}" type="number" groupingUsed="true"/> VND
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>
            </div>


            <div class="text-center mb-4">
                <button type="button" class="btn btn-outline-secondary" onclick="window.print();">
                    <i class="fas fa-print me-1"></i> In hợp đồng
                </button>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
