<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty order ? 'Tạo đơn hàng mới' : 'Sửa đơn hàng'} - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <link href="https://cdn.jsdelivr.net/npm/select2-bootstrap-5-theme@1.3.0/dist/select2-bootstrap-5-theme.min.css" rel="stylesheet" />
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

        .form-section-title {
            font-weight: 600;
            margin-bottom: 1rem;
            padding-bottom: 0.5rem;
            border-bottom: 2px solid #0d6efd;
            color: #0d6efd;
        }

        .item-row {
            background: #f8f9fa;
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 10px;
            border: 1px solid #e9ecef;
        }

        .item-row:hover {
            border-color: #0d6efd;
        }

        .remove-item-btn {
            color: #dc3545;
            cursor: pointer;
        }

        .remove-item-btn:hover {
            color: #bb2d3b;
        }

        .select2-container--bootstrap-5 .select2-selection {
            min-height: 38px;
        }

        .asset-info {
            font-size: 0.85rem;
            color: #6c757d;
        }

        #itemsContainer .item-row:only-child .remove-item-btn {
            display: none;
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
                <h4 class="mb-1">
                    <i class="fas fa-clipboard-list me-2"></i>
                    ${empty order ? 'Tạo đơn hàng mới' : 'Sửa đơn hàng'}
                </h4>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/sale/dashboard">Dashboard</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/sale/orders">Đơn hàng</a></li>
                        <li class="breadcrumb-item active">${empty order ? 'Tạo mới' : 'Sửa'}</li>
                    </ol>
                </nav>
            </div>
            <div class="d-flex align-items-center">
                <span class="me-3">
                    <i class="fas fa-user-circle me-1"></i> ${sessionScope.fullName}
                </span>
            </div>
        </div>

        <!-- Content -->
        <div class="container-fluid">
            <form method="post" action="${pageContext.request.contextPath}/sale/orders" id="orderForm">
                <input type="hidden" name="action" value="${empty order ? 'create' : 'update'}">
                <c:if test="${not empty order}">
                    <input type="hidden" name="id" value="${order.id}">
                </c:if>

                <div class="row">
                    <!-- Left Column - Order Info -->
                    <div class="col-md-5">
                        <div class="content-card">
                            <h6 class="form-section-title">
                                <i class="fas fa-info-circle me-2"></i>Thông tin đơn hàng
                            </h6>

                            <div class="mb-3">
                                <label class="form-label">Mã đơn hàng <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" name="orderCode"
                                       value="${not empty order ? order.orderCode : orderCode}"
                                       ${not empty order ? 'readonly' : ''} required>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Khách hàng <span class="text-danger">*</span></label>
                                <select class="form-select" name="customerId" id="customerSelect" required>
                                    <option value="">-- Chọn khách hàng --</option>
                                    <c:forEach items="${customers}" var="customer">
                                        <option value="${customer.id}"
                                                data-phone="${customer.phoneNumber}"
                                                data-email="${customer.email}"
                                                ${order.customerId == customer.id ? 'selected' : ''}>
                                            ${customer.fullName} (${customer.phoneNumber})
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                                    <input type="date" class="form-control" name="startDate"
                                           value="${order.startDate}" required>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">Ngày kết thúc</label>
                                    <input type="date" class="form-control" name="endDate"
                                           value="${order.endDate}">
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ghi chú</label>
                                <textarea class="form-control" name="note" rows="3">${order.note}</textarea>
                            </div>
                        </div>
                    </div>

                    <!-- Right Column - Machine Items -->
                    <div class="col-md-7">
                        <div class="content-card">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h6 class="form-section-title mb-0">
                                    <i class="fas fa-cogs me-2"></i>Danh sách máy cho thuê
                                </h6>
                                <button type="button" class="btn btn-success btn-sm" onclick="addItemRow()">
                                    <i class="fas fa-plus me-1"></i>Thêm máy
                                </button>
                            </div>

                            <div id="itemsContainer">
                                <c:choose>
                                    <c:when test="${not empty order && not empty order.items}">
                                        <c:forEach items="${order.items}" var="item" varStatus="loop">
                                            <div class="item-row" data-index="${loop.index}">
                                                <div class="d-flex justify-content-between align-items-start mb-2">
                                                    <strong>Máy #<span class="item-number">${loop.index + 1}</span></strong>
                                                    <span class="remove-item-btn" onclick="removeItemRow(this)">
                                                        <i class="fas fa-times-circle fa-lg"></i>
                                                    </span>
                                                </div>
                                                <div class="row">
                                                    <div class="col-md-6 mb-2">
                                                        <label class="form-label">Chọn máy <span class="text-danger">*</span></label>
                                                        <select class="form-select asset-select" name="assetIds" required>
                                                            <option value="">-- Chọn máy --</option>
                                                            <c:forEach items="${availableAssets}" var="asset">
                                                                <option value="${asset.id}"
                                                                        data-serial="${asset.serialNumber}"
                                                                        data-model="${asset.modelName}"
                                                                        data-brand="${asset.brand}"
                                                                        ${item.assetId == asset.id ? 'selected' : ''}>
                                                                    ${asset.serialNumber} - ${asset.modelName} (${asset.brand})
                                                                </option>
                                                            </c:forEach>
                                                            <!-- Keep the current selection even if not available anymore -->
                                                            <c:if test="${not empty item.assetId}">
                                                                <option value="${item.assetId}" selected>
                                                                    ${item.serialNumber} - ${item.modelName} (${item.brand})
                                                                </option>
                                                            </c:if>
                                                        </select>
                                                    </div>
                                                    <div class="col-md-6 mb-2">
                                                        <label class="form-label">Giá thuê (VNĐ)</label>
                                                        <input type="number" class="form-control" name="prices"
                                                               value="${item.price}" min="0" step="1000">
                                                    </div>
                                                </div>
                                                <div class="mb-0">
                                                    <label class="form-label">Ghi chú</label>
                                                    <input type="text" class="form-control" name="itemNotes"
                                                           value="${item.note}" placeholder="Ghi chú cho máy này">
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <!-- Default empty row -->
                                        <div class="item-row" data-index="0">
                                            <div class="d-flex justify-content-between align-items-start mb-2">
                                                <strong>Máy #<span class="item-number">1</span></strong>
                                                <span class="remove-item-btn" onclick="removeItemRow(this)">
                                                    <i class="fas fa-times-circle fa-lg"></i>
                                                </span>
                                            </div>
                                            <div class="row">
                                                <div class="col-md-6 mb-2">
                                                    <label class="form-label">Chọn máy <span class="text-danger">*</span></label>
                                                    <select class="form-select asset-select" name="assetIds" required>
                                                        <option value="">-- Chọn máy --</option>
                                                        <c:forEach items="${availableAssets}" var="asset">
                                                            <option value="${asset.id}"
                                                                    data-serial="${asset.serialNumber}"
                                                                    data-model="${asset.modelName}"
                                                                    data-brand="${asset.brand}">
                                                                ${asset.serialNumber} - ${asset.modelName} (${asset.brand})
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                </div>
                                                <div class="col-md-6 mb-2">
                                                    <label class="form-label">Giá thuê (VNĐ)</label>
                                                    <input type="number" class="form-control" name="prices"
                                                           min="0" step="1000">
                                                </div>
                                            </div>
                                            <div class="mb-0">
                                                <label class="form-label">Ghi chú</label>
                                                <input type="text" class="form-control" name="itemNotes"
                                                       placeholder="Ghi chú cho máy này">
                                            </div>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <div class="alert alert-info mt-3 mb-0">
                                <i class="fas fa-info-circle me-2"></i>
                                Chỉ hiển thị các máy đang sẵn sàng (ACTIVE + AVAILABLE).
                                Sau khi Manager duyệt, các máy sẽ chuyển sang trạng thái "Đang cho thuê".
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="content-card">
                    <div class="d-flex justify-content-between">
                        <a href="${pageContext.request.contextPath}/sale/orders" class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i>Quay lại
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-1"></i>${empty order ? 'Tạo đơn hàng' : 'Cập nhật'}
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>
        let itemIndex = ${not empty order && not empty order.items ? order.items.size() : 1};

        // Available assets data for dynamic rows
        const availableAssets = [
            <c:forEach items="${availableAssets}" var="asset" varStatus="loop">
            {
                id: ${asset.id},
                serial: '${asset.serialNumber}',
                model: '${asset.modelName}',
                brand: '${asset.brand}'
            }${!loop.last ? ',' : ''}
            </c:forEach>
        ];

        function addItemRow() {
            const container = document.getElementById('itemsContainer');
            const newRow = document.createElement('div');
            newRow.className = 'item-row';
            newRow.dataset.index = itemIndex;

            let optionsHtml = '<option value="">-- Chọn máy --</option>';
            availableAssets.forEach(asset => {
                optionsHtml += '<option value="' + asset.id + '" data-serial="' + asset.serial +
                               '" data-model="' + asset.model + '" data-brand="' + asset.brand + '">' +
                               asset.serial + ' - ' + asset.model + ' (' + asset.brand + ')</option>';
            });

            newRow.innerHTML =
                '<div class="d-flex justify-content-between align-items-start mb-2">' +
                '    <strong>Máy #<span class="item-number">' + (itemIndex + 1) + '</span></strong>' +
                '    <span class="remove-item-btn" onclick="removeItemRow(this)">' +
                '        <i class="fas fa-times-circle fa-lg"></i>' +
                '    </span>' +
                '</div>' +
                '<div class="row">' +
                '    <div class="col-md-6 mb-2">' +
                '        <label class="form-label">Chọn máy <span class="text-danger">*</span></label>' +
                '        <select class="form-select asset-select" name="assetIds" required>' +
                optionsHtml +
                '        </select>' +
                '    </div>' +
                '    <div class="col-md-6 mb-2">' +
                '        <label class="form-label">Giá thuê (VNĐ)</label>' +
                '        <input type="number" class="form-control" name="prices" min="0" step="1000">' +
                '    </div>' +
                '</div>' +
                '<div class="mb-0">' +
                '    <label class="form-label">Ghi chú</label>' +
                '    <input type="text" class="form-control" name="itemNotes" placeholder="Ghi chú cho máy này">' +
                '</div>';

            container.appendChild(newRow);
            itemIndex++;
            updateItemNumbers();
        }

        function removeItemRow(btn) {
            const row = btn.closest('.item-row');
            const container = document.getElementById('itemsContainer');

            if (container.children.length > 1) {
                row.remove();
                updateItemNumbers();
            }
        }

        function updateItemNumbers() {
            const rows = document.querySelectorAll('.item-row');
            rows.forEach((row, index) => {
                row.querySelector('.item-number').textContent = index + 1;
            });
        }

        // Validate form before submit
        document.getElementById('orderForm').addEventListener('submit', function(e) {
            const assetSelects = document.querySelectorAll('select[name="assetIds"]');
            const selectedAssets = [];
            let hasDuplicate = false;

            assetSelects.forEach(select => {
                if (select.value) {
                    if (selectedAssets.includes(select.value)) {
                        hasDuplicate = true;
                    }
                    selectedAssets.push(select.value);
                }
            });

            if (hasDuplicate) {
                e.preventDefault();
                alert('Không được chọn trùng máy! Vui lòng kiểm tra lại.');
                return false;
            }

            if (selectedAssets.length === 0) {
                e.preventDefault();
                alert('Vui lòng chọn ít nhất 1 máy.');
                return false;
            }
        });

        // Initialize Select2 for customer dropdown
        $(document).ready(function() {
            $('#customerSelect').select2({
                theme: 'bootstrap-5',
                placeholder: '-- Chọn khách hàng --',
                allowClear: true
            });
        });
    </script>
</body>
</html>
