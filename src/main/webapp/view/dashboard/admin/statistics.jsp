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
            <%-- Statistics Cards --%>
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
                <div class="stat-card warning">
                    <div class="stat-card-title">Yêu cầu đang chờ</div>
                    <div class="stat-card-value">${totalPendingRequests}</div>
                    <i class="fas fa-clock stat-card-icon"></i>
                </div>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card info">
                    <div class="stat-card-title">Tổng số máy</div>
                    <div class="stat-card-value">${totalMachines}</div>
                    <i class="fas fa-cogs stat-card-icon"></i>
                </div>
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng hợp đồng</div>
                    <div class="stat-card-value">${totalContracts}</div>
                    <i class="fas fa-file-contract stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Tổng bảo trì</div>
                    <div class="stat-card-value">${totalMaintenances}</div>
                    <i class="fas fa-wrench stat-card-icon"></i>
                </div>
                <div class="stat-card warning">
                    <div class="stat-card-title">Yêu cầu đã duyệt</div>
                    <div class="stat-card-value">${totalApprovedRequests}</div>
                    <i class="fas fa-check-circle stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Charts --%>
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Phân bố Người dùng theo Role</h5>
                        <canvas id="usersByRoleChart"></canvas>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Yêu cầu Đặt lại Mật khẩu</h5>
                        <canvas id="passwordResetChart"></canvas>
                    </div>
                </div>
            </div>
            
            <div class="row">
                <div class="col-lg-12 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-line me-2"></i>Hợp đồng theo Tháng (6 tháng gần nhất)</h5>
                        <canvas id="contractsByMonthChart"></canvas>
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
                        <h5 class="chart-title"><i class="fas fa-chart-bar me-2"></i>Tổng quan Hệ thống</h5>
                        <canvas id="systemOverviewChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Prepare data
        const usersByRole = {
            <c:forEach var="entry" items="${usersByRole}">
            "${entry.key}": ${entry.value},
            </c:forEach>
        };
        
        const contractsByMonth = {
            <c:forEach var="entry" items="${contractsByMonth}">
            "${entry.key}": ${entry.value},
            </c:forEach>
        };
        
        // Chart 1: Users by Role Pie Chart
        const usersByRoleCtx = document.getElementById('usersByRoleChart').getContext('2d');
        new Chart(usersByRoleCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(usersByRole),
                datasets: [{
                    data: Object.values(usersByRole),
                    backgroundColor: [
                        '#667eea',
                        '#f093fb',
                        '#4facfe',
                        '#43e97b',
                        '#fa709a'
                    ]
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
        
        // Chart 2: Password Reset Requests Pie Chart
        const passwordResetCtx = document.getElementById('passwordResetChart').getContext('2d');
        new Chart(passwordResetCtx, {
            type: 'pie',
            data: {
                labels: ['Đang chờ', 'Đã duyệt', 'Đã từ chối'],
                datasets: [{
                    data: [${totalPendingRequests}, ${totalApprovedRequests}, ${totalRejectedRequests}],
                    backgroundColor: ['#fee140', '#43e97b', '#f5576c']
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
        
        // Chart 3: Contracts by Month Line Chart
        const contractsByMonthCtx = document.getElementById('contractsByMonthChart').getContext('2d');
        new Chart(contractsByMonthCtx, {
            type: 'line',
            data: {
                labels: Object.keys(contractsByMonth),
                datasets: [{
                    label: 'Số hợp đồng',
                    data: Object.values(contractsByMonth),
                    borderColor: '#667eea',
                    backgroundColor: 'rgba(102, 126, 234, 0.1)',
                    tension: 0.4,
                    fill: true
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
        
        // Chart 4: User Status Bar Chart
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
        
        // Chart 5: System Overview Bar Chart
        const systemOverviewCtx = document.getElementById('systemOverviewChart').getContext('2d');
        new Chart(systemOverviewCtx, {
            type: 'bar',
            data: {
                labels: ['Máy', 'Hợp đồng', 'Bảo trì'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${totalMachines}, ${totalContracts}, ${totalMaintenances}],
                    backgroundColor: ['#667eea', '#4facfe', '#43e97b']
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
                        beginAtZero: true
                    }
                }
            }
        });
    </script>
</body>
</html>


