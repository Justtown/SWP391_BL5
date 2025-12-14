<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Sidebar -->
<div class="col-lg-2 col-md-3 mb-4">
    <div class="card">
        <div class="card-header bg-primary text-white">
            <i class="fas fa-th-list"></i> Machine Types
        </div>
        <ul class="list-group list-group-flush">
            <li class="list-group-item ${empty typeIdFilter || typeIdFilter == null ? 'active' : ''}">
                <a href="${pageContext.request.contextPath}/machines" 
                   class="text-decoration-none ${empty typeIdFilter || typeIdFilter == null ? 'text-white' : ''}">
                    <i class="fas fa-cogs"></i> All Machines
                </a>
            </li>
         
        </ul>
    </div>
    
   
</div>
