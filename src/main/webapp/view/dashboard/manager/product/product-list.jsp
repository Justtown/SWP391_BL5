<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Product List - Argo Machine Management</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
  <style>
    body {
      margin: 0;
      font-family: Arial, sans-serif;
      background: #f5f7fa;
    }
    .logo {
      margin-top: 20px;
      text-align: center;
    }
    .title {
      font-size: 18px;
      color: #1e4e79;
      margin-top: 5px;
      text-align: center;
    }
    .menu {
      margin-top: 15px;
      background: #1e4e79;
      padding: 12px 0;
      text-align: center;
    }
    .menu a {
      color: white;
      text-decoration: none;
      margin: 0 25px;
      font-size: 15px;
      font-weight: bold;
    }
    .menu a:hover { opacity: 0.7; }
    .footer {
      background: #1e4e79;
      color: white;
      padding: 15px 0;
      text-align: center;
      margin-top: 30px;
    }
  </style>
</head>
<body>

<%
  Integer userId = (Integer) session.getAttribute("userId");
  String username = (String) session.getAttribute("username");
  String fullName = (String) session.getAttribute("fullName");
  boolean isLoggedIn = (userId != null);
%>

<!-- Header -->
<div class="logo">
  <img src="${pageContext.request.contextPath}/images/logo.png" alt="Logo" style="height:100px;" onerror="this.style.display='none'">
</div>

<div class="title">Argo Machine Management</div>

<div class="menu">
  <a href="${pageContext.request.contextPath}/home">Home</a>
  <a href="${pageContext.request.contextPath}/product-list">Product</a>
  <a href="${pageContext.request.contextPath}/introduce">Introduce</a>

  <% if (isLoggedIn) { %>
  <a href="${pageContext.request.contextPath}/my-profile">Profile</a>
  <a href="${pageContext.request.contextPath}/change-password">Change Password</a>
  <a href="#" data-bs-toggle="modal" data-bs-target="#logoutModal">Logout (<%= fullName != null ? fullName : username %>)</a>
  <% } else { %>
  <a href="${pageContext.request.contextPath}/login">Login</a>
  <% } %>
</div>

<!-- Main Content -->
<div class="container-fluid py-4">
  <div class="row">
    <!-- Sidebar -->
    <div class="col-lg-2 col-md-3 mb-4">
      <div class="card">
        <div class="card-header bg-primary text-white">
          <i class="fas fa-th-list"></i> Categories
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item active"><a href="#" class="text-decoration-none text-white">All Products</a></li>
          <li class="list-group-item"><a href="#" class="text-decoration-none">Fertilizer</a></li>
          <li class="list-group-item"><a href="#" class="text-decoration-none">Pesticide</a></li>
          <li class="list-group-item"><a href="#" class="text-decoration-none">Seeds</a></li>
          <li class="list-group-item"><a href="#" class="text-decoration-none">Organic Fertilizer</a></li>
        </ul>
      </div>
    </div>

    <!-- Content Area -->
    <div class="col-lg-10 col-md-9">
      <div class="card">
        <div class="card-body">
          <h4 class="card-title mb-4">Product List</h4>

          <!-- Filter Section -->
          <div class="row g-3 mb-4">
            <div class="col-md-3">
              <input type="text" class="form-control" placeholder="Product name / SKU-CODE">
            </div>
            <div class="col-md-2">
              <select class="form-select">
                <option value="">Category</option>
                <option value="fertilizer">Fertilizer</option>
                <option value="pesticide">Pesticide</option>
                <option value="seeds">Seeds</option>
                <option value="organic">Organic Fertilizer</option>
              </select>
            </div>
            <div class="col-md-2">
              <select class="form-select">
                <option value="">Status</option>
                <option value="active">Active</option>
                <option value="low-stock">Low Stock</option>
                <option value="out-of-stock">Out of Stock</option>
                <option value="near-expiry">Near Expiry</option>
              </select>
            </div>
            <div class="col-md-2">
              <button type="button" class="btn btn-outline-primary">
                <i class="fas fa-search"></i> Filter
              </button>
            </div>
          </div>

          <!-- Product Table -->
          <div class="table-responsive">
            <table class="table table-bordered table-hover">
              <thead class="table-light">
              <tr>
                <th>[#]</th>
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
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
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
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
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
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
                </td>
              </tr>
              <tr>
                <td>4</td>
                <td>Neem Oil Organic</td>
                <td>SKU-004</td>
                <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
                <td>Organic Fertilizer</td>
                <td>GreenFarm</td>
                <td>6.90</td>
                <td>8</td>
                <td><span class="badge bg-warning text-dark">Low Stock</span></td>
                <td>
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
                </td>
              </tr>
              <tr>
                <td>5</td>
                <td>Urea 46% Nitrogen</td>
                <td>SKU-005</td>
                <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
                <td>Fertilizer</td>
                <td>VNAgro</td>
                <td>14.00</td>
                <td>200</td>
                <td><span class="badge bg-success">Active</span></td>
                <td>
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
                </td>
              </tr>
              <tr>
                <td>6</td>
                <td>Carbendazim 50WP</td>
                <td>SKU-006</td>
                <td><img src="https://via.placeholder.com/50" alt="Product" class="img-thumbnail" style="width:50px;height:50px;"></td>
                <td>Pesticide</td>
                <td>ChemPro</td>
                <td>2.50</td>
                <td>0</td>
                <td><span class="badge bg-danger">Out of Stock</span></td>
                <td>
                  <a href="#" class="btn btn-sm btn-info">View</a>
                  <a href="#" class="btn btn-sm btn-warning">Edit</a>
                </td>
              </tr>
              </tbody>
            </table>
          </div>

          <!-- Pagination -->
          <nav>
            <ul class="pagination justify-content-center">
              <li class="page-item">
                <a class="page-link" href="#">&lt;</a>
              </li>
              <li class="page-item active"><a class="page-link" href="#">1</a></li>
              <li class="page-item"><a class="page-link" href="#">2</a></li>
              <li class="page-item"><a class="page-link" href="#">3</a></li>
              <li class="page-item disabled"><a class="page-link" href="#">...</a></li>
              <li class="page-item">
                <a class="page-link" href="#">&gt;</a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Footer -->
<div class="footer">
  <span>&copy; 2024 Argo Machine Management. All rights reserved.</span>
</div>

<!-- Logout Modal -->
<% if (isLoggedIn) { %>
<%@ include file="/view/authen/logout.jsp" %>
<% } %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
