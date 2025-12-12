
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Create Service Order</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-4">
    <h3>Create Service Order (Hợp đồng gia công)</h3>
    <form action="${pageContext.request.contextPath}/orders" method="post" class="mt-3">
        <input type="hidden" name="action" value="create"/>

        <div class="mb-3">
            <label class="form-label">Contract Code</label>
            <input type="text" name="contractCode" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Customer Name</label>
            <input type="text" name="customerName" class="form-control" required>
        </div>

        <div class="mb-3">
            <label class="form-label">Customer Phone</label>
            <input type="text" name="customerPhone" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Customer Address</label>
            <input type="text" name="customerAddress" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Machine ID (nếu có)</label>
            <input type="number" name="machineId" class="form-control">
        </div>

        <div class="mb-3">
            <label class="form-label">Service Description</label>
            <textarea name="serviceDescription" class="form-control" rows="3"></textarea>
        </div>

        <div class="row">
            <div class="mb-3 col-md-6">
                <label class="form-label">Start Date</label>
                <input type="date" name="startDate" class="form-control">
            </div>
            <div class="mb-3 col-md-6">
                <label class="form-label">End Date</label>
                <input type="date" name="endDate" class="form-control">
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Status</label>
            <select name="status" class="form-select">
                <option value="PENDING">PENDING</option>
                <option value="IN_PROGRESS">IN_PROGRESS</option>
                <option value="DONE">DONE</option>
                <option value="CANCELLED">CANCELLED</option>
            </select>
        </div>

        <div class="mb-3">
            <label class="form-label">Total Cost (VNĐ)</label>
            <input type="number" step="0.01" name="totalCost" class="form-control">
        </div>

        <button type="submit" class="btn btn-primary">Create Order</button>
        <a href="${pageContext.request.contextPath}/orders?action=list" class="btn btn-secondary">Back to List</a>
    </form>
</div>
</body>
</html>

