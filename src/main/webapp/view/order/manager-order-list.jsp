<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Quản lý đơn hàng - Argo Machine Management</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    .badge-pending { background-color: #ffc107; }
    .badge-approved { background-color: #28a745; }
    .badge-rejected { background-color: #dc3545; }
    .badge-in-progress { background-color: #17a2b8; }
    .badge-completed { background-color: #6c757d; }
    .badge-cancelled { background-color: #343a40; }
  </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />
<div class="main-content">
<div class="container-fluid mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3><i class="bi bi-cart-check"></i> Quản lý đơn hàng</h3>
  </div>

  <!-- Success/Error Messages -->
  <c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.success == 'approved'}">Duyệt đơn hàng thành công!</c:when>
        <c:when test="${param.success == 'rejected'}">Từ chối đơn hàng thành công!</c:when>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  
  <c:if test="${param.error != null}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.error == 'notfound'}">Không tìm thấy đơn hàng!</c:when>
        <c:when test="${param.error == 'invalid'}">Dữ liệu không hợp lệ!</c:when>
        <c:when test="${param.error == 'failed'}">Thao tác thất bại. Vui lòng thử lại!</c:when>
        <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại!</c:otherwise>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <!-- Search Form -->
  <div class="card mb-3">
    <div class="card-body">
      <form action="${pageContext.request.contextPath}/manager/orders" method="get" class="row g-3">
        <input type="hidden" name="action" value="list"/>
        
        <div class="col-md-3">
          <label class="form-label">Mã hợp đồng</label>
          <input type="text" name="searchContract" class="form-control" 
                 placeholder="Nhập mã hợp đồng..." value="${param.searchContract}">
        </div>
        
        <div class="col-md-3">
          <label class="form-label">Khách hàng</label>
          <input type="text" name="searchCustomer" class="form-control" 
                 placeholder="Nhập tên khách hàng..." value="${param.searchCustomer}">
        </div>
        
        <div class="col-md-3">
          <label class="form-label">Trạng thái</label>
          <select name="searchStatus" class="form-control">
            <option value="">-- Tất cả --</option>
            <option value="PENDING" ${param.searchStatus == 'PENDING' ? 'selected' : ''}>Chờ duyệt</option>
            <option value="APPROVED" ${param.searchStatus == 'APPROVED' ? 'selected' : ''}>Đã duyệt</option>
            <option value="REJECTED" ${param.searchStatus == 'REJECTED' ? 'selected' : ''}>Từ chối</option>
          </select>
        </div>
        
        <div class="col-md-3 d-flex align-items-end">
          <button type="submit" class="btn btn-primary me-2">
            <i class="bi bi-search"></i> Tìm kiếm
          </button>
          <a href="${pageContext.request.contextPath}/manager/orders?action=list" class="btn btn-secondary">
            <i class="bi bi-x-circle"></i> Xóa bộ lọc
          </a>
        </div>
      </form>
    </div>
  </div>

  <div class="table-responsive">
    <table class="table table-striped table-bordered table-hover">
      <thead class="table-dark">
      <tr>
        <th>Mã HĐ</th>
        <th>Khách hàng</th>
        <th>SĐT</th>
        <th>Mô tả dịch vụ</th>
        <th>Ngày bắt đầu</th>
        <th>Ngày kết thúc</th>
        <th>Tổng tiền</th>
        <th>Trạng thái</th>
        <th>Người tạo</th>
        <th>Người duyệt</th>
        <th>Hành động</th>
      </tr>
      </thead>
      <tbody>
      <c:forEach var="o" items="${orders}">
        <tr>
          <td><strong>${o.contractCode}</strong></td>
          <td>${o.customerName}</td>
          <td>${o.customerPhone}</td>
          <td>
            <c:choose>
              <c:when test="${o.serviceDescription.length() > 50}">
                ${o.serviceDescription.substring(0, 50)}...
              </c:when>
              <c:otherwise>
                ${o.serviceDescription}
              </c:otherwise>
            </c:choose>
          </td>
          <td><fmt:formatDate value="${o.startDate}" pattern="dd/MM/yyyy"/></td>
          <td><fmt:formatDate value="${o.endDate}" pattern="dd/MM/yyyy"/></td>
          <td><fmt:formatNumber value="${o.totalCost}" type="currency" currencySymbol="đ" groupingUsed="true"/></td>
          <td>
            <c:choose>
              <c:when test="${o.status == 'PENDING'}">
                <span class="badge badge-pending">Chờ duyệt</span>
              </c:when>
              <c:when test="${o.status == 'APPROVED'}">
                <span class="badge badge-approved">Đã duyệt</span>
              </c:when>
              <c:when test="${o.status == 'REJECTED'}">
                <span class="badge badge-rejected">Từ chối</span>
              </c:when>
              <c:otherwise>
                <span class="badge bg-secondary">${o.status}</span>
              </c:otherwise>
            </c:choose>
          </td>
          <td><small>${o.createdByName}</small></td>
          <td><small>${o.approvedByName != null ? o.approvedByName : '-'}</small></td>
          <td>
            <div class="btn-group btn-group-sm" role="group">
              <a href="${pageContext.request.contextPath}/manager/orders?action=detail&id=${o.id}" 
                 class="btn btn-info" title="Xem chi tiết">
                <i class="bi bi-eye"></i>
              </a>
              
              <c:if test="${o.status == 'PENDING'}">
                <button type="button" class="btn btn-success" title="Duyệt"
                        onclick="if(confirm('Duyệt đơn hàng này?')) { 
                          location.href='${pageContext.request.contextPath}/manager/orders?action=approve&id=${o.id}'; }">
                  <i class="bi bi-check-circle"></i>
                </button>
                <button type="button" class="btn btn-danger" title="Từ chối"
                        onclick="if(confirm('Từ chối đơn hàng này?')) { 
                          location.href='${pageContext.request.contextPath}/manager/orders?action=reject&id=${o.id}'; }">
                  <i class="bi bi-x-circle"></i>
                </button>
              </c:if>
            </div>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty orders}">
        <tr>
          <td colspan="11" class="text-center text-muted">Không có đơn hàng nào</td>
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
