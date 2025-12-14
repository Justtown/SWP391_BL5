
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Service Order List</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
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
<body class="bg-light">
<div class="container-fluid mt-4">
  <div class="d-flex justify-content-between align-items-center mb-3">
    <h3><i class="bi bi-cart-check"></i> Danh sách đơn hàng</h3>
    <div>
      <c:if test="${sessionScope.userRole == 'sale' || sessionScope.userRole == 'admin' || sessionScope.roleName == 'sale' || sessionScope.roleName == 'admin'}">
        <a href="${pageContext.request.contextPath}/sale/orders?action=create" class="btn btn-success btn-lg">
          <i class="bi bi-plus-circle-fill"></i> Thêm đơn hàng mới
        </a>
      </c:if>
    </div>
  </div>

  <!-- Success/Error Messages -->
  <c:if test="${param.success != null}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.success == 'created'}">Tạo đơn hàng thành công!</c:when>
        <c:when test="${param.success == 'updated'}">Cập nhật đơn hàng thành công!</c:when>
        <c:when test="${param.success == 'deleted'}">Xóa đơn hàng thành công!</c:when>
        <c:when test="${param.success == 'approved'}">Duyệt đơn hàng thành công!</c:when>
        <c:when test="${param.success == 'rejected'}">Từ chối đơn hàng thành công!</c:when>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>
  
  <c:if test="${param.error != null}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
      <c:choose>
        <c:when test="${param.error == 'permission'}">Bạn không có quyền thực hiện hành động này!</c:when>
        <c:when test="${param.error == 'notfound'}">Không tìm thấy đơn hàng!</c:when>
        <c:when test="${param.error == 'cannot_edit'}">Không thể chỉnh sửa đơn hàng đã được duyệt!</c:when>
        <c:when test="${param.error == 'cannot_delete'}">Không thể xóa đơn hàng đã được duyệt!</c:when>
        <c:otherwise>Có lỗi xảy ra. Vui lòng thử lại!</c:otherwise>
      </c:choose>
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

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
              <c:when test="${o.status == 'IN_PROGRESS'}">
                <span class="badge badge-in-progress">Đang xử lý</span>
              </c:when>
              <c:when test="${o.status == 'COMPLETED'}">
                <span class="badge badge-completed">Hoàn thành</span>
              </c:when>
              <c:when test="${o.status == 'CANCELLED'}">
                <span class="badge badge-cancelled">Đã hủy</span>
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
              <a href="${pageContext.request.contextPath}/sale/orders?action=detail&id=${o.id}" 
                 class="btn btn-info" title="Xem chi tiết">
                <i class="bi bi-eye"></i>
              </a>
              
              <c:if test="${userRole == 'sale' && o.status == 'PENDING'}">
                <a href="${pageContext.request.contextPath}/sale/orders?action=edit&id=${o.id}" 
                   class="btn btn-warning" title="Chỉnh sửa">
                  <i class="bi bi-pencil"></i>
                </a>
                <button type="button" class="btn btn-danger" title="Xóa"
                        onclick="if(confirm('Bạn có chắc muốn xóa đơn hàng này?')) { 
                          location.href='${pageContext.request.contextPath}/sale/orders?action=delete&id=${o.id}'; }">
                  <i class="bi bi-trash"></i>
                </button>
              </c:if>
              
              <c:if test="${userRole == 'admin' && o.status == 'PENDING'}">
                <button type="button" class="btn btn-success" title="Duyệt"
                        onclick="if(confirm('Duyệt đơn hàng này?')) { 
                          var form = document.createElement('form'); 
                          form.method = 'POST'; 
                          form.action = '${pageContext.request.contextPath}/sale/orders'; 
                          var input1 = document.createElement('input'); 
                          input1.type = 'hidden'; input1.name = 'action'; input1.value = 'approve'; 
                          var input2 = document.createElement('input'); 
                          input2.type = 'hidden'; input2.name = 'id'; input2.value = '${o.id}'; 
                          form.appendChild(input1); form.appendChild(input2); 
                          document.body.appendChild(form); form.submit(); }">
                  <i class="bi bi-check-circle"></i>
                </button>
                <button type="button" class="btn btn-danger" title="Từ chối"
                        onclick="if(confirm('Từ chối đơn hàng này?')) { 
                          var form = document.createElement('form'); 
                          form.method = 'POST'; 
                          form.action = '${pageContext.request.contextPath}/sale/orders'; 
                          var input1 = document.createElement('input'); 
                          input1.type = 'hidden'; input1.name = 'action'; input1.value = 'reject'; 
                          var input2 = document.createElement('input'); 
                          input2.type = 'hidden'; input2.name = 'id'; input2.value = '${o.id}'; 
                          form.appendChild(input1); form.appendChild(input2); 
                          document.body.appendChild(form); form.submit(); }">
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

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>

