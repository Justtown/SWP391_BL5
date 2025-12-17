<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thống Kê - Manager Dashboard</title>
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
        .section-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: #495057;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e9ecef;
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
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Thống Kê</li>
                    </ol>
                </nav>
            </div>
            <a href="${pageContext.request.contextPath}/manager/dashboard" class="btn btn-outline-secondary btn-sm">
                <i class="fas fa-arrow-left me-1"></i>Quay lại
            </a>
        </div>
        
        <div class="container-fluid">
            <%-- Statistics Cards --%>
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng số máy</div>
                    <div class="stat-card-value">${totalMachines}</div>
                    <i class="fas fa-cogs stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Đang hoạt động</div>
                    <div class="stat-card-value">${activeMachines}</div>
                    <i class="fas fa-check-circle stat-card-icon"></i>
                </div>
                <div class="stat-card warning">
                    <div class="stat-card-title">Có thể cho thuê</div>
                    <div class="stat-card-value">${rentableMachines}</div>
                    <i class="fas fa-hand-holding stat-card-icon"></i>
                </div>
                <div class="stat-card danger">
                    <div class="stat-card-title">Không hoạt động</div>
                    <div class="stat-card-value">${inactiveMachines}</div>
                    <i class="fas fa-times-circle stat-card-icon"></i>
                </div>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card primary">
                    <div class="stat-card-title">Tổng hợp đồng</div>
                    <div class="stat-card-value">${totalContracts}</div>
                    <i class="fas fa-file-contract stat-card-icon"></i>
                </div>
                <div class="stat-card success">
                    <div class="stat-card-title">Đang active</div>
                    <div class="stat-card-value">${activeContracts}</div>
                    <i class="fas fa-check-circle stat-card-icon"></i>
                </div>
                <div class="stat-card info">
                    <div class="stat-card-title">Hoàn thành</div>
                    <div class="stat-card-value">${finishedContracts}</div>
                    <i class="fas fa-check-double stat-card-icon"></i>
                </div>
                <div class="stat-card warning">
                    <div class="stat-card-title">Tổng bảo trì</div>
                    <div class="stat-card-value">${totalMaintenances}</div>
                    <i class="fas fa-wrench stat-card-icon"></i>
                </div>
            </div>
            
            <%-- Charts --%>
            <div class="row">
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Phân bố Máy theo Loại</h5>
                        <canvas id="machineTypeChart"></canvas>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-pie me-2"></i>Phân bố Bảo trì theo Loại</h5>
                        <canvas id="maintenanceTypeChart"></canvas>
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
                        <h5 class="chart-title"><i class="fas fa-chart-bar me-2"></i>Trạng thái Máy</h5>
                        <canvas id="machineStatusChart"></canvas>
                    </div>
                </div>
                <div class="col-lg-6 mb-4">
                    <div class="chart-container">
                        <h5 class="chart-title"><i class="fas fa-chart-bar me-2"></i>Trạng thái Hợp đồng</h5>
                        <canvas id="contractStatusChart"></canvas>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Prepare data for charts
        const machinesByType = {
            <c:forEach var="entry" items="${machinesByType}">
            "${entry.key}": ${entry.value},
            </c:forEach>
        };
        
        const maintenancesByType = {
            <c:forEach var="entry" items="${maintenancesByType}">
            "${entry.key}": ${entry.value},
            </c:forEach>
        };
        
        const contractsByMonth = {
            <c:forEach var="entry" items="${contractsByMonth}">
            "${entry.key}": ${entry.value},
            </c:forEach>
        };
        
        // Chart 1: Machine Type Pie Chart
        const machineTypeCtx = document.getElementById('machineTypeChart').getContext('2d');
        new Chart(machineTypeCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(machinesByType),
                datasets: [{
                    data: Object.values(machinesByType),
                    backgroundColor: [
                        '#667eea',
                        '#f093fb',
                        '#4facfe',
                        '#43e97b',
                        '#fa709a',
                        '#fee140',
                        '#a8edea',
                        '#fed6e3'
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
        
        // Chart 2: Maintenance Type Pie Chart
        const maintenanceTypeCtx = document.getElementById('maintenanceTypeChart').getContext('2d');
        new Chart(maintenanceTypeCtx, {
            type: 'pie',
            data: {
                labels: Object.keys(maintenancesByType),
                datasets: [{
                    data: Object.values(maintenancesByType),
                    backgroundColor: [
                        '#667eea',
                        '#f093fb',
                        '#4facfe',
                        '#43e97b',
                        '#fa709a',
                        '#fee140'
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
        
        // Chart 4: Machine Status Bar Chart
        const machineStatusCtx = document.getElementById('machineStatusChart').getContext('2d');
        new Chart(machineStatusCtx, {
            type: 'bar',
            data: {
                labels: ['Hoạt động', 'Không hoạt động', 'Có thể thuê'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${activeMachines}, ${inactiveMachines}, ${rentableMachines}],
                    backgroundColor: ['#43e97b', '#f5576c', '#4facfe']
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
        
        // Chart 5: Contract Status Bar Chart
        const contractStatusCtx = document.getElementById('contractStatusChart').getContext('2d');
        new Chart(contractStatusCtx, {
            type: 'bar',
            data: {
                labels: ['Active', 'Finished', 'Draft', 'Cancelled'],
                datasets: [{
                    label: 'Số lượng',
                    data: [${activeContracts}, ${finishedContracts}, ${draftContracts}, ${cancelledContracts}],
                    backgroundColor: ['#43e97b', '#4facfe', '#fee140', '#f5576c']
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

