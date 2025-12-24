<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống Kê - Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
    <style>
        body {
            background-color: #f8f9fa;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
        }
        .page-header {
            background: white;
            border-bottom: 1px solid #dee2e6;
            padding: 1rem 1.5rem;
            margin-bottom: 1.5rem;
        }
        .stats-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
            padding: 30px;
            margin-bottom: 20px;
        }
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        .stat-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 12px;
            padding: 25px;
            color: white;
            position: relative;
            overflow: hidden;
            transition: transform 0.2s;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .stat-card.primary { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
        .stat-card.success { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
        .stat-card.warning { background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); }
        .stat-card.danger { background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); }
        .stat-card.info { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
        .stat-card-title {
            font-size: 0.85rem;
            opacity: 0.9;
            margin-bottom: 10px;
            font-weight: 500;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }
        .stat-card-value {
            font-size: 2.5rem;
            font-weight: 700;
            margin-bottom: 5px;
        }
        .stat-card-icon {
            position: absolute;
            right: 20px;
            top: 50%;
            transform: translateY(-50%);
            font-size: 4rem;
            opacity: 0.2;
        }
        .chart-container {
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            margin-bottom: 30px;
        }
        .chart-title {
            font-size: 1.1rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <div class="main-content">
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-chart-bar me-2"></i>Thống Kê Chi Tiết</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Thống Kê</li>
                    </ol>
                </nav>
            </div>
            <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i>Quay lại
            </a>
        </div>
        
        <div class="container-fluid">
            <%-- Statistics Cards - Basic Only --%>
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng người dùng</div>
                    <div class="stat-card-value">${totalUsers}</div>
                    <i class="fas fa-users stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Người dùng active</div>
                    <div class="stat-card-value">${activeUsers}</div>
                    <i class="fas fa-user-check stat-card-icon"></i>
                </div>
                <div class="stat-card danger">
                    <div class="stat-card-title">Người dùng inactive</div>
                    <div class="stat-card-value">${inactiveUsers}</div>
                    <i class="fas fa-user-times stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Machine Assets Statistics --%>
            <div class="stats-grid">
                <div class="stat-card info">
                    <div class="stat-card-title">Tổng số máy</div>
                    <div class="stat-card-value">${totalAssets}</div>
                    <i class="fas fa-cogs stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Máy sẵn sàng</div>
                    <div class="stat-card-value">${availableAssets}</div>
                    <i class="fas fa-check-circle stat-card-icon"></i>
                </div>
                <div class="stat-card warning">
                    <div class="stat-card-title">Máy đang thuê</div>
                    <div class="stat-card-value">${rentedAssets}</div>
                    <i class="fas fa-hand-holding-usd stat-card-icon"></i>
                </div>
                <div class="stat-card danger">
                    <div class="stat-card-title">Máy bảo trì</div>
                    <div class="stat-card-value">${maintenanceAssets}</div>
                    <i class="fas fa-wrench stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Contracts Statistics --%>
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng hợp đồng</div>
                    <div class="stat-card-value">${totalContracts}</div>
                    <i class="fas fa-file-contract stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Hợp đồng Active</div>
                    <div class="stat-card-value">${activeContracts}</div>
                    <i class="fas fa-play-circle stat-card-icon"></i>
                </div>
                <div class="stat-card info">
                    <div class="stat-card-title">Hợp đồng Finished</div>
                    <div class="stat-card-value">${finishedContracts}</div>
                    <i class="fas fa-check-double stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Orders Statistics --%>
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng đơn hàng</div>
                    <div class="stat-card-value">${totalOrders}</div>
                    <i class="fas fa-shopping-cart stat-card-icon"></i>
                </div>
                <div class="stat-card warning">
                    <div class="stat-card-title">Đơn chờ duyệt</div>
                    <div class="stat-card-value">${pendingOrders}</div>
                    <i class="fas fa-clock stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Đơn đã duyệt</div>
                    <div class="stat-card-value">${approvedOrders}</div>
                    <i class="fas fa-check-circle stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Charts - Basic Only --%>
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Trạng thái Máy</h5>
                        <canvas id="assetRentalStatusChart"></canvas>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Trạng thái Hợp đồng</h5>
                        <canvas id="contractStatusChart"></canvas>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-bar me-2"></i>Trạng thái Người dùng</h5>
                        <canvas id="userStatusChart"></canvas>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-bar me-2"></i>Trạng thái Đơn hàng</h5>
                        <canvas id="orderStatusChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Chart 1: Asset Rental Status Doughnut Chart
        const assetRentalStatusCtx = document.getElementById('assetRentalStatusChart').getContext('2d');
        new Chart(assetRentalStatusCtx, {
            type: 'doughnut',
            data: {
                labels: ['Sẵn sàng', 'Đang thuê', 'Bảo trì'],
                datasets: [{
                    data: [${availableAssets}, ${rentedAssets}, ${maintenanceAssets}],
                    backgroundColor: ['#28a745', '#ffc107', '#dc3545']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
        
        // Chart 2: Contract Status Pie Chart
        const contractStatusCtx = document.getElementById('contractStatusChart').getContext('2d');
        new Chart(contractStatusCtx, {
            type: 'pie',
            data: {
                labels: ['Active', 'Finished'],
                datasets: [{
                    data: [${activeContracts}, ${finishedContracts}],
                    backgroundColor: ['#28a745', '#6c757d']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
        
        // Chart 3: User Status Bar Chart
        const userStatusCtx = document.getElementById('userStatusChart').getContext('2d');
        new Chart(userStatusCtx, {
            type: 'bar',
            data: {
                labels: ['Active', 'Inactive'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${activeUsers}, ${inactiveUsers}],
                    backgroundColor: ['#43e97b', '#f5576c']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
        
        // Chart 4: Order Status Bar Chart
        const orderStatusCtx = document.getElementById('orderStatusChart').getContext('2d');
        new Chart(orderStatusCtx, {
            type: 'bar',
            data: {
                labels: ['Chờ duyệt', 'Đã duyệt'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${pendingOrders}, ${approvedOrders}],
                    backgroundColor: ['#ffc107', '#28a745']
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: {
                            stepSize: 1
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>


