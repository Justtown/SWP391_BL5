<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Product Management - Argo Machine Management</title>
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
  </style>
</head>
<body>
<jsp:include page="/view/common/dashboard/sideBar.jsp" />

<div class="main-content">
  <!-- Page Header -->
  <div class="page-header d-flex justify-content-between align-items-center">
    <div>
      <h4 class="mb-1"><i class="fas fa-box me-2"></i>Quản lý sản phẩm</h4>
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb mb-0">
          <li class="breadcrumb-item">
            <a href="${pageContext.request.contextPath}/${sessionScope.roleName}/dashboard">Dashboard</a>
          </li>
          <li class="breadcrumb-item active">Products</li>
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

  <div class="container-fluid">
    <div class="content-card">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h5 class="mb-0">Danh sách sản phẩm</h5>
        <button type="button" class="btn btn-primary" disabled
                title="Chức năng tạo sản phẩm sẽ bổ sung sau">
          <i class="fas fa-plus"></i> Thêm sản phẩm mới
        </button>
      </div>

      <!-- Filters (UI template) -->
      <div class="filter-section">
        <input type="text" class="form-control" style="max-width: 280px;" placeholder="Product name / SKU-CODE">
        <select class="form-select" style="max-width: 180px;">
          <option value="">Category</option>
          <option value="fertilizer">Fertilizer</option>
          <option value="pesticide">Pesticide</option>
          <option value="seeds">Seeds</option>
          <option value="organic">Organic Fertilizer</option>
        </select>
        <select class="form-select" style="max-width: 180px;">
          <option value="">Status</option>
          <option value="active">Active</option>
          <option value="low-stock">Low Stock</option>
          <option value="out-of-stock">Out of Stock</option>
          <option value="near-expiry">Near Expiry</option>
        </select>
        <button type="button" class="btn btn-outline-primary">
          <i class="fas fa-filter"></i> Lọc
        </button>
      </div>

      <!-- Product Table (static demo data for now) -->
      <div class="table-responsive">
        <table class="table table-bordered table-hover">
          <thead class="table-light">
          <tr>
            <th>#</th>
            <th>Product name</th>
            <th>SKU_CODE</th>
            <th>Thumbnail</th>
            <th>Category</th>
            <th>Supplier</th>
            <th>Sale price</th>
            <th>Stock</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
          </thead>
          <tbody>
          <tr>
            <td>1</td>
            <td>NPK Fertilizer 16-16-8</td>
            <td>SKU-001</td>
            <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
            <td>Fertilizer</td>
            <td>VNAgro</td>
            <td>18.50</td>
            <td>120</td>
            <td><span class="badge bg-success">Active</span></td>
            <td>
              <button type="button" class="btn btn-sm btn-info" disabled><i class="fas fa-eye"></i></button>
              <button type="button" class="btn btn-sm btn-warning" disabled><i class="fas fa-edit"></i></button>
            </td>
          </tr>
          <tr>
            <td>2</td>
            <td>Glyphosate 480EC</td>
            <td>SKU-002</td>
            <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
            <td>Pesticide</td>
            <td>AgroChem</td>
            <td>9.20</td>
            <td>35</td>
            <td><span class="badge bg-warning text-dark">Near Expiry</span></td>
            <td>
              <button type="button" class="btn btn-sm btn-info" disabled><i class="fas fa-eye"></i></button>
              <button type="button" class="btn btn-sm btn-warning" disabled><i class="fas fa-edit"></i></button>
            </td>
          </tr>
          <tr>
            <td>3</td>
            <td>Hybrid Corn Seed H99</td>
            <td>SKU-003</td>
            <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
            <td>Seeds</td>
            <td>SeedWorld</td>
            <td>3.10</td>
            <td>500</td>
            <td><span class="badge bg-success">Active</span></td>
            <td>
              <button type="button" class="btn btn-sm btn-info" disabled><i class="fas fa-eye"></i></button>
              <button type="button" class="btn btn-sm btn-warning" disabled><i class="fas fa-edit"></i></button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
