<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Loại Máy - Argo Machine Management</title>
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
        
        .search-container {
            flex: 1 1 auto;
            position: relative;
            min-width: 200px;
        }
        
        .search-input {
            padding-left: 40px;
        }
        
        .search-icon {
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            color: #6c757d;
        }
        
        .modal-card {
            border-radius: 10px;
            box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
        }
        
        .type-card {
            background: white;
            border: 1px solid #e9ecef;
            border-radius: 8px;
            padding: 1.25rem;
            margin-bottom: 1rem;
            transition: all 0.2s;
        }
        
        .type-card:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            border-color: #0d6efd;
        }
        
        .type-icon {
            width: 50px;
            height: 50px;
            background: linear-gradient(135deg, #0d6efd, #6610f2);
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-size: 1.25rem;
        }
        
        .machine-count-badge {
            background: #e9ecef;
            color: #495057;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 500;
        }
        
        /* Stats Cards */
        .stats-card {
            background: white;
            border-radius: 12px;
            padding: 1.25rem;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            border: none;
            transition: transform 0.2s;
        }
        
        .stats-card:hover {
            transform: translateY(-2px);
        }
        
        .stats-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 1.25rem;
        }
        
        .stats-icon.primary { background: rgba(13, 110, 253, 0.1); color: #0d6efd; }
        .stats-icon.success { background: rgba(25, 135, 84, 0.1); color: #198754; }
        .stats-icon.warning { background: rgba(255, 193, 7, 0.15); color: #cc9a06; }
        .stats-icon.danger { background: rgba(220, 53, 69, 0.1); color: #dc3545; }
        
        .stats-value {
            font-size: 1.75rem;
            font-weight: 700;
            color: #212529;
        }
        
        .stats-label {
            color: #6c757d;
            font-size: 0.875rem;
        }
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />
    
    <!-- Main Content -->
    <div class="main-content">
        <!-- Page Header -->
        <div class="page-header d-flex justify-content-between align-items-center">
            <div>
                <h4 class="mb-1"><i class="fas fa-layer-group me-2"></i>Quản Lý Loại Máy</h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item active">Loại Máy</li>
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
        
        <!-- Content -->
        <div class="container-fluid">
            <!-- Alert Messages -->
            <c:if test="${not empty successMsg}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle"></i> ${successMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty errorMsg}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle"></i> ${errorMsg}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            
            <!-- Statistics Cards -->
            <div class="row mb-4">
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="stats-card">
                        <div class="d-flex align-items-center">
                            <div class="stats-icon primary me-3">
                                <i class="fas fa-layer-group"></i>
                            </div>
                            <div>
                                <div class="stats-value">${totalTypes}</div>
                                <div class="stats-label">Loại máy</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="stats-card">
                        <div class="d-flex align-items-center">
                            <div class="stats-icon success me-3">
                                <i class="fas fa-cogs"></i>
                            </div>
                            <div>
                                <div class="stats-value">${totalMachines}</div>
                                <div class="stats-label">Tổng số máy</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="stats-card">
                        <div class="d-flex align-items-center">
                            <div class="stats-icon warning me-3">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <div>
                                <div class="stats-value">${activeMachines}</div>
                                <div class="stats-label">Đang hoạt động</div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-md-3 col-sm-6 mb-3">
                    <div class="stats-card">
                        <div class="d-flex align-items-center">
                            <div class="stats-icon danger me-3">
                                <i class="fas fa-times-circle"></i>
                            </div>
                            <div>
                                <div class="stats-value">${inactiveMachines}</div>
                                <div class="stats-label">Không hoạt động</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <!-- Machine Types Card -->
            <div class="content-card">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                    <h5 class="mb-0">Danh sách loại máy</h5>
                    <div class="d-flex gap-2">
                        <button type="button" class="btn btn-success" id="btnExportExcel">
                            <i class="fas fa-file-excel"></i> Xuất Excel
                        </button>
                        <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#createTypeModal">
                            <i class="fas fa-plus"></i> Thêm loại máy
                        </button>
                    </div>
                </div>
                        
                        <!-- Search -->
                        <form method="GET" action="${pageContext.request.contextPath}/manager/machine-types">
                            <div class="filter-section">
                                <div class="search-container">
                                    <i class="fas fa-search search-icon"></i>
                                    <input type="text" class="form-control search-input" name="keyword" 
                                           placeholder="Tìm theo tên hoặc mô tả..." value="${keyword}">
                                </div>
                                <button type="submit" class="btn btn-outline-primary">
                                    <i class="fas fa-search"></i> Tìm kiếm
                                </button>
                            </div>
                        </form>
                        
                        <!-- Machine Types Grid -->
                        <div class="row">
                            <c:choose>
                                <c:when test="${not empty machineTypes && machineTypes.size() > 0}">
                                    <c:forEach var="type" items="${machineTypes}">
                                        <div class="col-md-6 col-lg-4">
                                            <div class="type-card">
                                                <div class="d-flex align-items-start">
                                                    <div class="type-icon me-3">
                                                        <i class="fas fa-cog"></i>
                                                    </div>
                                                    <div class="flex-grow-1">
                                                        <h6 class="mb-1">${type.typeName}</h6>
                                                        <p class="text-muted small mb-2">
                                                            ${type.description != null ? type.description : 'Không có mô tả'}
                                                        </p>
                                                        <span class="machine-count-badge">
                                                            <i class="fas fa-cogs me-1"></i>
                                                            ${machineCounts[type.id]} máy
                                                        </span>
                                                    </div>
                                                    <div class="dropdown">
                                                        <button class="btn btn-sm btn-light" data-bs-toggle="dropdown">
                                                            <i class="fas fa-ellipsis-v"></i>
                                                        </button>
                                                        <ul class="dropdown-menu dropdown-menu-end">
                                                            <li>
                                                                <a class="dropdown-item btn-edit-type" href="#" 
                                                                   data-id="${type.id}">
                                                                    <i class="fas fa-edit me-2"></i>Chỉnh sửa
                                                                </a>
                                                            </li>
                                                            <li>
                                                                <a class="dropdown-item btn-view-machines" href="#" 
                                                                   data-id="${type.id}" data-name="${type.typeName}">
                                                                    <i class="fas fa-list me-2"></i>Xem danh sách máy
                                                                </a>
                                                            </li>
                                                            <li><hr class="dropdown-divider"></li>
                                                            <li>
                                                                <a class="dropdown-item text-danger btn-delete-type" href="#" 
                                                                   data-id="${type.id}" data-name="${type.typeName}"
                                                                   data-count="${machineCounts[type.id]}">
                                                                    <i class="fas fa-trash me-2"></i>Xóa
                                                                </a>
                                                            </li>
                                                        </ul>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <div class="col-12">
                                        <div class="text-center py-5 text-muted">
                                            <i class="fas fa-inbox fa-3x mb-3"></i>
                                            <p>Không tìm thấy loại máy nào</p>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- ==================== MODALS ==================== -->
    
    <!-- CREATE TYPE MODAL -->
    <div class="modal fade" id="createTypeModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content modal-card">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-plus-circle"></i> Thêm loại máy mới
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machine-types" method="POST">
                    <input type="hidden" name="action" value="create">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="createTypeName" class="form-label">Tên loại máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-tag"></i></span>
                                <input type="text" class="form-control" id="createTypeName" name="typeName" 
                                       required placeholder="VD: Máy kéo">
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="createDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="createDescription" name="description" 
                                      rows="3" placeholder="Nhập mô tả loại máy..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save"></i> Tạo loại máy
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- EDIT TYPE MODAL -->
    <div class="modal fade" id="editTypeModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content modal-card">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title">
                        <i class="fas fa-edit"></i> Chỉnh sửa loại máy
                    </h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machine-types" method="POST">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" id="editTypeId">
                    <div class="modal-body">
                        <div class="mb-3">
                            <label for="editTypeName" class="form-label">Tên loại máy <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="fas fa-tag"></i></span>
                                <input type="text" class="form-control" id="editTypeName" name="typeName" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label for="editDescription" class="form-label">Mô tả</label>
                            <textarea class="form-control" id="editDescription" name="description" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-warning">
                            <i class="fas fa-save"></i> Lưu thay đổi
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- DELETE TYPE MODAL -->
    <div class="modal fade" id="deleteTypeModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content modal-card">
                <div class="modal-header bg-danger text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-exclamation-triangle"></i> Xác nhận xóa
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form action="${pageContext.request.contextPath}/manager/machine-types" method="POST">
                    <input type="hidden" name="action" value="delete">
                    <input type="hidden" name="id" id="deleteTypeId">
                    <div class="modal-body">
                        <div class="text-center mb-3">
                            <i class="fas fa-trash fa-3x text-danger"></i>
                        </div>
                        <p class="text-center">
                            Bạn có chắc chắn muốn xóa loại máy:<br>
                            <strong id="deleteTypeName">-</strong>?
                        </p>
                        <div class="alert alert-warning" id="deleteWarning" style="display: none;">
                            <i class="fas fa-info-circle"></i>
                            <span id="deleteWarningText"></span>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Hủy
                        </button>
                        <button type="submit" class="btn btn-danger" id="confirmDeleteBtn">
                            <i class="fas fa-trash"></i> Xóa
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    
    <!-- VIEW MACHINES MODAL -->
    <div class="modal fade" id="viewMachinesModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg">
            <div class="modal-content modal-card">
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title">
                        <i class="fas fa-list"></i> Danh sách máy - <span id="viewMachinesTypeName"></span>
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="machinesListContent">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Loading...</span>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Đóng
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const contextPath = '${pageContext.request.contextPath}';
            
            // Export Excel
            document.getElementById('btnExportExcel').addEventListener('click', function() {
                const data = [
                    ['STT', 'Tên loại máy', 'Mô tả', 'Số lượng máy']
                ];
                
                <c:forEach var="type" items="${machineTypes}" varStatus="loop">
                data.push([
                    ${loop.index + 1},
                    '${type.typeName}',
                    '${type.description != null ? type.description : ""}',
                    ${machineCounts[type.id]}
                ]);
                </c:forEach>
                
                const ws = XLSX.utils.aoa_to_sheet(data);
                const wb = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws, 'Danh sách loại máy');
                
                // Set column widths
                ws['!cols'] = [
                    { wch: 5 },
                    { wch: 25 },
                    { wch: 40 },
                    { wch: 15 }
                ];
                
                XLSX.writeFile(wb, 'danh_sach_loai_may.xlsx');
            });
            
            // Edit Type
            document.querySelectorAll('.btn-edit-type').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const id = this.getAttribute('data-id');
                    
                    fetch(contextPath + '/manager/machine-types?action=detail&id=' + id)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                const type = data.machineType;
                                document.getElementById('editTypeId').value = type.id;
                                document.getElementById('editTypeName').value = type.typeName || '';
                                document.getElementById('editDescription').value = type.description || '';
                                new bootstrap.Modal(document.getElementById('editTypeModal')).show();
                            } else {
                                alert('Lỗi: ' + data.message);
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('Không thể tải thông tin loại máy');
                        });
                });
            });
            
            // Delete Type
            document.querySelectorAll('.btn-delete-type').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const id = this.getAttribute('data-id');
                    const name = this.getAttribute('data-name');
                    const count = parseInt(this.getAttribute('data-count'));
                    
                    document.getElementById('deleteTypeId').value = id;
                    document.getElementById('deleteTypeName').textContent = name;
                    
                    const warningDiv = document.getElementById('deleteWarning');
                    const confirmBtn = document.getElementById('confirmDeleteBtn');
                    
                    if (count > 0) {
                        warningDiv.style.display = 'block';
                        document.getElementById('deleteWarningText').textContent = 
                            'Không thể xóa loại máy này vì còn ' + count + ' máy đang sử dụng.';
                        confirmBtn.disabled = true;
                    } else {
                        warningDiv.style.display = 'none';
                        confirmBtn.disabled = false;
                    }
                    
                    new bootstrap.Modal(document.getElementById('deleteTypeModal')).show();
                });
            });
            
            // View Machines by Type
            document.querySelectorAll('.btn-view-machines').forEach(function(btn) {
                btn.addEventListener('click', function(e) {
                    e.preventDefault();
                    const typeId = this.getAttribute('data-id');
                    const typeName = this.getAttribute('data-name');
                    
                    document.getElementById('viewMachinesTypeName').textContent = typeName;
                    document.getElementById('machinesListContent').innerHTML = 
                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';
                    
                    new bootstrap.Modal(document.getElementById('viewMachinesModal')).show();
                    
                    fetch(contextPath + '/manager/machine-types?action=machines&typeId=' + typeId)
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                renderMachinesList(data.machines);
                            } else {
                                document.getElementById('machinesListContent').innerHTML = 
                                    '<div class="alert alert-danger">Lỗi: ' + data.message + '</div>';
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            document.getElementById('machinesListContent').innerHTML = 
                                '<div class="alert alert-danger">Không thể tải danh sách máy</div>';
                        });
                });
            });
            
            function renderMachinesList(machines) {
                if (machines.length === 0) {
                    document.getElementById('machinesListContent').innerHTML = 
                        '<div class="text-center py-4 text-muted"><i class="fas fa-inbox fa-2x mb-2"></i><br>Không có máy nào thuộc loại này</div>';
                    return;
                }
                
                let html = '<table class="table table-bordered table-hover"><thead class="table-light">';
                html += '<tr><th>#</th><th>Mã máy</th><th>Tên máy</th><th>Trạng thái</th><th>Vị trí</th></tr></thead><tbody>';
                
                machines.forEach(function(machine, index) {
                    let statusBadge = '';
                    if (machine.status === 'ACTIVE') {
                        statusBadge = '<span class="badge bg-success">Hoạt động</span>';
                    } else if (machine.status === 'INACTIVE') {
                        statusBadge = '<span class="badge bg-danger">Không hoạt động</span>';
                    } else {
                        statusBadge = '<span class="badge bg-secondary">Ngừng sử dụng</span>';
                    }
                    
                    html += '<tr>';
                    html += '<td>' + (index + 1) + '</td>';
                    html += '<td><strong>' + machine.machineCode + '</strong></td>';
                    html += '<td>' + machine.machineName + '</td>';
                    html += '<td>' + statusBadge + '</td>';
                    html += '<td>' + (machine.location || '-') + '</td>';
                    html += '</tr>';
                });
                
                html += '</tbody></table>';
                document.getElementById('machinesListContent').innerHTML = html;
            }
        });
    </script>
</body>
</html>

