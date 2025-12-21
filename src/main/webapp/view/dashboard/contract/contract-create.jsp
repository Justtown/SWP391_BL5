<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${editMode ? 'Sửa' : 'Tạo'} Hợp đồng - Argo Machine Management</title>
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

        .item-row {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 1rem;
            margin-bottom: 0.5rem;
        }

        .item-row:hover {
            background-color: #e9ecef;
        }

        .remove-item-btn {
            cursor: pointer;
            color: #dc3545;
        }

        .remove-item-btn:hover {
            color: #a71d2a;
        }

        .asset-option {
            padding: 10px;
            cursor: pointer;
            border-bottom: 1px solid #eee;
        }

        .asset-option:hover {
            background-color: #f8f9fa;
        }

        .asset-option.selected {
            background-color: #d4edda;
        }

        .total-price {
            font-size: 1.5rem;
            font-weight: bold;
            color: #28a745;
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
                <i class="fas fa-file-contract me-2"></i>
                ${editMode ? 'Sửa' : 'Tạo'} Hợp đồng
            </h4>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/dashboard">Dashboard</a></li>
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/manager/contracts">Hợp đồng</a></li>
                    <li class="breadcrumb-item active">${editMode ? 'Sửa' : 'Tạo mới'}</li>
                </ol>
            </nav>
        </div>
        <div>
            <a href="${pageContext.request.contextPath}/manager/contracts" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left me-1"></i> Quay lại
            </a>
        </div>
    </div>

    <!-- Content -->
    <div class="container-fluid">
        <!-- Cảnh báo nếu không còn máy available -->
        <c:if test="${empty availableAssets && !editMode}">
            <div class="alert alert-danger d-flex align-items-center" role="alert">
                <i class="fas fa-exclamation-triangle me-3 fa-2x"></i>
                <div>
                    <h5 class="alert-heading mb-1">Không thể tạo hợp đồng!</h5>
                    <p class="mb-0">Hiện tại không còn máy nào sẵn sàng cho thuê. Vui lòng bổ sung hoặc cập nhật trạng thái máy.</p>
                </div>
                <a href="${pageContext.request.contextPath}/manager/contracts" class="btn btn-outline-danger ms-auto">
                    <i class="fas fa-arrow-left me-1"></i> Quay lại
                </a>
            </div>
        </c:if>

        <c:if test="${not empty availableAssets || editMode}">
            <form method="post" action="${pageContext.request.contextPath}/manager/contracts" id="contractForm">
                <input type="hidden" name="action" value="${editMode ? 'update' : 'create'}">
                <c:if test="${editMode}">
                    <input type="hidden" name="id" value="${contract.id}">
                </c:if>

                <!-- Contract Info -->
                <div class="content-card">
                    <h5 class="mb-4"><i class="fas fa-info-circle me-2"></i>Thông tin hợp đồng</h5>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Mã hợp đồng</label>
                            <input type="text" class="form-control" name="contractCode"
                                   value="${editMode ? contract.contractCode : contractCode}" readonly>
                            <small class="text-muted">Mã được tạo tự động</small>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Khách hàng <span class="text-danger">*</span></label>
                            <select class="form-select" name="customerId" required>
                                <option value="">-- Chọn khách hàng --</option>
                                <c:forEach items="${customers}" var="customer">
                                    <option value="${customer.id}"
                                        ${editMode && contract.customerId == customer.id ? 'selected' : ''}>
                                            ${customer.fullName} (${customer.email})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                            <input type="date" class="form-control" name="startDate"
                                   value="${editMode ? contract.startDate : ''}" required>
                        </div>
                        <div class="col-md-6 mb-3">
                            <label class="form-label">Ngày kết thúc</label>
                            <input type="date" class="form-control" name="endDate"
                                   value="${editMode ? contract.endDate : ''}">
                        </div>
                    </div>

                    <c:if test="${not editMode}">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Trạng thái ban đầu</label>
                                <select class="form-select" name="status">
                                    <option value="DRAFT">Nháp (DRAFT)</option>
                                    <option value="ACTIVE">Kích hoạt ngay (ACTIVE)</option>
                                </select>
                                <small class="text-muted">Chọn ACTIVE sẽ ngay lập tức cập nhật trạng thái máy thành "Đang cho thuê"</small>
                            </div>
                        </div>
                    </c:if>

                    <div class="mb-3">
                        <label class="form-label">Ghi chú</label>
                        <textarea class="form-control" name="note" rows="3"
                                  placeholder="Nhập ghi chú (nếu có)...">${editMode ? contract.note : ''}</textarea>
                    </div>
                </div>

                <!-- Contract Items -->
                <div class="content-card">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h5 class="mb-0"><i class="fas fa-cogs me-2"></i>Danh sách máy thuê</h5>
                        <button type="button" class="btn btn-success" onclick="addItem()">
                            <i class="fas fa-plus me-1"></i> Thêm máy
                        </button>
                    </div>

                    <div id="itemsContainer">
                        <c:choose>
                            <c:when test="${editMode && not empty contract.items}">
                                <c:forEach items="${contract.items}" var="item" varStatus="loop">
                                    <div class="item-row" id="item-${loop.index}">
                                        <div class="row align-items-center">
                                            <div class="col-md-5">
                                                <label class="form-label">Máy</label>
                                                <select class="form-select asset-select" name="assetId[]" required
                                                        onchange="updateTotal()">
                                                    <option value="">-- Chọn máy --</option>
                                                    <c:forEach items="${availableAssets}" var="asset">
                                                        <option value="${asset.id}"
                                                            ${item.assetId == asset.id ? 'selected' : ''}>
                                                                ${asset.serialNumber} - ${asset.modelName} (${asset.brand})
                                                        </option>
                                                    </c:forEach>
                                                    <!-- Keep current item if not in available list -->
                                                    <c:set var="found" value="false"/>
                                                    <c:forEach items="${availableAssets}" var="asset">
                                                        <c:if test="${asset.id == item.assetId}">
                                                            <c:set var="found" value="true"/>
                                                        </c:if>
                                                    </c:forEach>
                                                    <c:if test="${!found}">
                                                        <option value="${item.assetId}" selected>
                                                                ${item.serialNumber} - ${item.modelName} (${item.brand}) [Hiện tại]
                                                        </option>
                                                    </c:if>
                                                </select>
                                            </div>
                                            <div class="col-md-3">
                                                <label class="form-label">Giá thuê (VND)</label>
                                                <input type="number" class="form-control price-input" name="price[]"
                                                       value="${item.price}" min="0" step="1000"
                                                       onchange="updateTotal()" placeholder="0">
                                            </div>
                                            <div class="col-md-3">
                                                <label class="form-label">Ghi chú</label>
                                                <input type="text" class="form-control" name="itemNote[]"
                                                       value="${item.note}" placeholder="Ghi chú...">
                                            </div>
                                            <div class="col-md-1 text-center">
                                                <label class="form-label">&nbsp;</label>
                                                <div>
                                                    <span class="remove-item-btn" onclick="removeItem('item-${loop.index}')">
                                                        <i class="fas fa-trash-alt fa-lg"></i>
                                                    </span>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <!-- Empty item template -->
                                <div class="item-row" id="item-0">
                                    <div class="row align-items-center">
                                        <div class="col-md-5">
                                            <label class="form-label">Máy</label>
                                            <select class="form-select asset-select" name="assetId[]" required
                                                    onchange="updateTotal()">
                                                <option value="">-- Chọn máy --</option>
                                                <c:forEach items="${availableAssets}" var="asset">
                                                    <option value="${asset.id}">
                                                            ${asset.serialNumber} - ${asset.modelName} (${asset.brand})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Giá thuê (VND)</label>
                                            <input type="number" class="form-control price-input" name="price[]"
                                                   min="0" step="1000" onchange="updateTotal()" placeholder="0">
                                        </div>
                                        <div class="col-md-3">
                                            <label class="form-label">Ghi chú</label>
                                            <input type="text" class="form-control" name="itemNote[]" placeholder="Ghi chú...">
                                        </div>
                                        <div class="col-md-1 text-center">
                                            <label class="form-label">&nbsp;</label>
                                            <div>
                                                <span class="remove-item-btn" onclick="removeItem('item-0')">
                                                    <i class="fas fa-trash-alt fa-lg"></i>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="alert alert-info mt-3" id="noItemsAlert" style="display: none;">
                        <i class="fas fa-info-circle me-2"></i>
                        Vui lòng thêm ít nhất một máy vào hợp đồng.
                    </div>

                    <!-- Total -->
                    <div class="row mt-4">
                        <div class="col-md-6 offset-md-6">
                            <div class="d-flex justify-content-between align-items-center p-3 bg-light rounded">
                                <span class="fw-bold">Tổng giá trị:</span>
                                <span class="total-price" id="totalPrice">0 VND</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="content-card">
                    <div class="d-flex justify-content-end gap-2">
                        <a href="${pageContext.request.contextPath}/manager/contracts" class="btn btn-secondary">
                            <i class="fas fa-times me-1"></i> Hủy
                        </a>
                        <button type="submit" class="btn btn-primary">
                            <i class="fas fa-save me-1"></i> ${editMode ? 'Cập nhật' : 'Tạo hợp đồng'}
                        </button>
                    </div>
                </div>
            </form>
        </c:if>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let itemCounter = ${editMode && not empty contract.items ? contract.items.size() : 1};

    // Available assets data for JavaScript
    const availableAssets = [
        <c:forEach items="${availableAssets}" var="asset" varStatus="loop">
        {
            id: ${asset.id},
            serialNumber: '${asset.serialNumber}',
            modelName: '${asset.modelName}',
            brand: '${asset.brand}'
        }${!loop.last ? ',' : ''}
        </c:forEach>
    ];

    function addItem() {
        const container = document.getElementById('itemsContainer');
        const newItem = document.createElement('div');
        newItem.className = 'item-row';
        newItem.id = 'item-' + itemCounter;

        let optionsHtml = '<option value="">-- Chọn máy --</option>';
        availableAssets.forEach(asset => {
            optionsHtml += '<option value="' + asset.id + '">' +
                asset.serialNumber + ' - ' + asset.modelName + ' (' + asset.brand + ')' +
                '</option>';
        });

        newItem.innerHTML =
            '<div class="row align-items-center">' +
            '    <div class="col-md-5">' +
            '        <label class="form-label">Máy</label>' +
            '        <select class="form-select asset-select" name="assetId[]" required onchange="updateTotal()">' +
            optionsHtml +
            '        </select>' +
            '    </div>' +
            '    <div class="col-md-3">' +
            '        <label class="form-label">Giá thuê (VND)</label>' +
            '        <input type="number" class="form-control price-input" name="price[]"' +
            '               min="0" step="1000" onchange="updateTotal()" placeholder="0">' +
            '    </div>' +
            '    <div class="col-md-3">' +
            '        <label class="form-label">Ghi chú</label>' +
            '        <input type="text" class="form-control" name="itemNote[]" placeholder="Ghi chú...">' +
            '    </div>' +
            '    <div class="col-md-1 text-center">' +
            '        <label class="form-label">&nbsp;</label>' +
            '        <div>' +
            '            <span class="remove-item-btn" onclick="removeItem(\'item-' + itemCounter + '\')">' +
            '                <i class="fas fa-trash-alt fa-lg"></i>' +
            '            </span>' +
            '        </div>' +
            '    </div>' +
            '</div>';

        container.appendChild(newItem);
        itemCounter++;
        checkItemsCount();
    }

    function removeItem(itemId) {
        const items = document.querySelectorAll('.item-row');
        if (items.length <= 1) {
            alert('Hợp đồng phải có ít nhất một máy!');
            return;
        }

        const item = document.getElementById(itemId);
        if (item) {
            item.remove();
            updateTotal();
            checkItemsCount();
        }
    }

    function updateTotal() {
        let total = 0;
        const priceInputs = document.querySelectorAll('.price-input');
        priceInputs.forEach(input => {
            const value = parseFloat(input.value) || 0;
            total += value;
        });

        document.getElementById('totalPrice').textContent =
            new Intl.NumberFormat('vi-VN').format(total) + ' VND';
    }

    function checkItemsCount() {
        const items = document.querySelectorAll('.item-row');
        const noItemsAlert = document.getElementById('noItemsAlert');
        if (items.length === 0) {
            noItemsAlert.style.display = 'block';
        } else {
            noItemsAlert.style.display = 'none';
        }
    }

    // Form validation
    document.getElementById('contractForm').addEventListener('submit', function(e) {
        // Validate ngày bắt đầu < ngày kết thúc
        const startDate = document.querySelector('input[name="startDate"]').value;
        const endDate = document.querySelector('input[name="endDate"]').value;

        if (startDate && endDate) {
            if (new Date(startDate) >= new Date(endDate)) {
                e.preventDefault();
                alert('Ngày bắt đầu phải trước ngày kết thúc!');
                return false;
            }
        }

        const assetSelects = document.querySelectorAll('.asset-select');
        let hasAsset = false;

        assetSelects.forEach(select => {
            if (select.value) {
                hasAsset = true;
            }
        });

        if (!hasAsset) {
            e.preventDefault();
            alert('Vui lòng chọn ít nhất một máy cho hợp đồng!');
            return false;
        }

        // Check for duplicate assets
        const selectedAssets = [];
        let hasDuplicate = false;

        assetSelects.forEach(select => {
            if (select.value) {
                if (selectedAssets.includes(select.value)) {
                    hasDuplicate = true;
                } else {
                    selectedAssets.push(select.value);
                }
            }
        });

        if (hasDuplicate) {
            e.preventDefault();
            alert('Không được chọn trùng máy trong cùng một hợp đồng!');
            return false;
        }
    });

    // Initial total calculation
    updateTotal();
    checkItemsCount();
</script>
</body>
</html>
