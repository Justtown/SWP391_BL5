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
    <title>Send Request</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-4">
    <h3>Send Request</h3>

    <form method="post" action="${pageContext.request.contextPath}/rent-request">

        <!-- Machine select -->
        <div class="mb-3">
            <label>Machine</label>
            <select name="machineId" class="form-control" required>
                <c:forEach var="m" items="${machines}">
                    <option value="${m.id}">${m.machineName} (${m.machineCode})</option>
                </c:forEach>
            </select>
        </div>

        <!-- Start Date -->
        <div class="mb-3">
            <label>Start Date</label>
            <input type="date" name="startDate" class="form-control" required>
        </div>

        <!-- End Date -->
        <div class="mb-3">
            <label>End Date</label>
            <input type="date" name="endDate" class="form-control" required>
        </div>

        <!-- Note -->
        <div class="mb-3">
            <label>Note</label>
            <textarea name="note" class="form-control" rows="3"></textarea>
        </div>

        <button type="submit" class="btn btn-primary">Submit</button>
    </form>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
