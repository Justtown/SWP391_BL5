<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Machine to Contract - Argo Machine Management</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        .add-machine-container {
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 30px;
        }
    </style>
</head>
<body>
    <jsp:include page="/view/common/dashboard/sideBar.jsp" />

    <div class="main-content">
        <div class="container-fluid">
            <div class="add-machine-container">
                <h2 class="mb-4">
                    <i class="fas fa-plus-circle me-2"></i>Add Machine to Contract
                </h2>
                
                <div class="mb-3">
                    <strong>Contract Code:</strong> ${contract.contractCode}
                </div>
                
                <c:if test="${not empty param.error}">
                    <div class="alert alert-danger">
                        <i class="fas fa-exclamation-circle me-2"></i>${param.error}
                    </div>
                </c:if>
                
                <c:choose>
                    <c:when test="${not empty machines && machines.size() > 0}">
                        <c:set var="contractsPath" value="/contracts" />
                        <c:if test="${sessionScope.roleName == 'manager'}">
                            <c:set var="contractsPath" value="/manager/contracts" />
                        </c:if>
                        <c:if test="${sessionScope.roleName == 'sale'}">
                            <c:set var="contractsPath" value="/sale/contracts" />
                        </c:if>
                        <form action="${pageContext.request.contextPath}${contractsPath}" method="POST">
                            <input type="hidden" name="action" value="add-machine">
                            <input type="hidden" name="contractId" value="${contract.id}">
                            
                            <div class="mb-3">
                                <label for="machineId" class="form-label">Select Machine <span class="text-danger">*</span></label>
                                <select class="form-select" id="machineId" name="machineId" required>
                                    <option value="">-- Select a machine --</option>
                                    <c:forEach var="machine" items="${machines}">
                                        <option value="${machine.id}">
                                            ${machine.machineCode} - ${machine.machineName} 
                                            (${machine.machineTypeName != null ? machine.machineTypeName : 'N/A'})
                                            - ${machine.status}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <div class="mb-3">
                                <label for="note" class="form-label">Note (Optional)</label>
                                <textarea class="form-control" id="note" name="note" rows="3" 
                                          placeholder="Enter any additional notes about this machine in the contract..."></textarea>
                            </div>
                            
                            <div class="d-flex justify-content-between">
                                <c:set var="contractsPath" value="/contracts" />
                                <c:if test="${sessionScope.roleName == 'manager'}">
                                    <c:set var="contractsPath" value="/manager/contracts" />
                                </c:if>
                                <a href="${pageContext.request.contextPath}${contractsPath}?action=detail&id=${contract.id}" 
                                   class="btn btn-secondary">
                                    <i class="fas fa-arrow-left me-1"></i> Cancel
                                </a>
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save me-1"></i> Add Machine
                                </button>
                            </div>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            No available machines to add. All machines may have already been added to this contract.
                        </div>
                        <c:set var="contractsPath" value="/contracts" />
                        <c:if test="${sessionScope.roleName == 'manager'}">
                            <c:set var="contractsPath" value="/manager/contracts" />
                        </c:if>
                        <a href="${pageContext.request.contextPath}${contractsPath}?action=detail&id=${contract.id}" 
                           class="btn btn-secondary">
                            <i class="fas fa-arrow-left me-1"></i> Back to Contract Detail
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
