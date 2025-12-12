
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Service Order List</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-4">
  <div class="d-flex justify-content-between align-items-center">
    <h3>Service Orders (Danh sách hợp đồng)</h3>
    <a href="${pageContext.request.contextPath}/orders?action=create" class="btn btn-primary">
      Create new order
    </a>
  </div>

  <table class="table table-striped table-bordered mt-3">
    <thead class="table-dark">
    <tr>
      <th>ID</th>
      <th>Contract Code</th>
      <th>Customer</th>
      <th>Phone</th>
      <th>Machine ID</th>
      <th>Status</th>
      <th>Start</th>
      <th>End</th>
      <th>Total Cost</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="o" items="${orders}">
      <tr>
        <td>${o.id}</td>
        <td>${o.contractCode}</td>
        <td>${o.customerName}</td>
        <td>${o.customerPhone}</td>
        <td>${o.machineId}</td>
        <td>${o.status}</td>
        <td>${o.startDate}</td>
        <td>${o.endDate}</td>
        <td>${o.totalCost}</td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
</div>
</body>
</html>

