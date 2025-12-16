<%--
  Created by IntelliJ IDEA.
  User: pc
  Date: 12/14/2025
  Time: 9:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Request List</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center">
        <h3>Request List</h3>
        <a href="${pageContext.request.contextPath}/rent-request?action=create"
           class="btn btn-primary">
            Send new request
        </a>

    </div>

    <table class="table table-bordered table-striped mt-3">
        <thead class="table-dark">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Created By</th>
            <th>Status</th>
            <th>Created At</th>
            <th width="180">Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="r" items="${requests}">
            <tr>
                <td>${r.id}</td>
                <td>${r.title}</td>
                <td>${r.customerId}</td>
                <td>
                    <span class="badge
                        ${r.status == 'PENDING' ? 'bg-warning' :
                          r.status == 'APPROVED' ? 'bg-success' : 'bg-danger'}">
                            ${r.status}
                    </span>
                </td>
                <td>${r.createdAt}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/requests?action=detail&id=${r.id}"
                       class="btn btn-sm btn-info">View</a>

                    <c:if test="${r.status == 'PENDING'}">
                        <form action="${pageContext.request.contextPath}/admin/rent-requests" method="post" style="display:inline">
                            <input type="hidden" name="id" value="${r.id}">
                            <input type="hidden" name="action" value="approve">
                            <button class="btn btn-sm btn-success">Approve</button>
                        </form>

                        <form action="${pageContext.request.contextPath}/admin/rent-requests" method="post" style="display:inline">
                            <input type="hidden" name="id" value="${r.id}">
                            <input type="hidden" name="action" value="decline">
                            <button class="btn btn-sm btn-danger">Decline</button>
                        </form>

                    </c:if>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</div>

</body>
</html>
