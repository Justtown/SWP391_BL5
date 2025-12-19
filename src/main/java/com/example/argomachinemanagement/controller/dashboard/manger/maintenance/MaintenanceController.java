package com.example.argomachinemanagement.controller.dashboard.manger.maintenance;

import com.example.argomachinemanagement.dal.MaintenanceDAO;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.entity.Maintenance;
import com.example.argomachinemanagement.entity.Machine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MaintenanceController", urlPatterns = {"/manager/maintenances"})
public class MaintenanceController extends HttpServlet {
    
    private static final int PAGE_SIZE = 4;
    
    private MaintenanceDAO maintenanceDAO;
    private MachineDAO machineDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        maintenanceDAO = new MaintenanceDAO();
        machineDAO = new MachineDAO();
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleList(request, response);
        } else if (action.equals("detail")) {
            handleDetail(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager/maintenances");
            return;
        }
        
        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "delete":
                handleDelete(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/maintenances");
        }
    }
    
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String machineIdStr = request.getParameter("machineId");
        String maintenanceType = request.getParameter("maintenanceType");
        String status = request.getParameter("status");
        String pageStr = request.getParameter("page");
        
        Integer machineId = null;
        if (machineIdStr != null && !machineIdStr.isEmpty()) {
            try {
                machineId = Integer.parseInt(machineIdStr);
            } catch (NumberFormatException e) {}
        }
        
        // Pagination
        int currentPage = 1;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            } catch (NumberFormatException e) {}
        }
        
        List<Maintenance> allMaintenances;
        if (machineId != null || (maintenanceType != null && !maintenanceType.isEmpty()) 
            || (status != null && !status.isEmpty())) {
            allMaintenances = maintenanceDAO.findByFilters(machineId, maintenanceType, status);
        } else {
            allMaintenances = maintenanceDAO.findAll();
        }
        
        // Pagination logic
        int totalRecords = allMaintenances.size();
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        if (currentPage > totalPages && totalPages > 0) currentPage = totalPages;
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalRecords);
        
        List<Maintenance> maintenances = new java.util.ArrayList<>();
        if (startIndex < totalRecords) {
            maintenances = allMaintenances.subList(startIndex, endIndex);
        }
        
        List<Machine> machines = machineDAO.findAll();
        List<String> maintenanceTypes = Arrays.asList(Maintenance.MAINTENANCE_TYPES);
        
        // Statistics
        int total = maintenanceDAO.countAll();
        int completed = maintenanceDAO.countByStatus("COMPLETED");
        int pending = maintenanceDAO.countByStatus("PENDING");
        
        request.setAttribute("maintenances", maintenances);
        request.setAttribute("machines", machines);
        request.setAttribute("maintenanceTypes", maintenanceTypes);
        request.setAttribute("totalCount", total);
        request.setAttribute("completedCount", completed);
        request.setAttribute("pendingCount", pending);
        
        // Pagination attributes
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("startIndex", startIndex);
        request.setAttribute("pageSize", PAGE_SIZE);
        // Preserve filters
        request.setAttribute("filterMachineId", machineId);
        request.setAttribute("filterType", maintenanceType);
        request.setAttribute("filterStatus", status);
        
        // Messages
        String successMsg = (String) request.getSession().getAttribute("successMsg");
        String errorMsg = (String) request.getSession().getAttribute("errorMsg");
        if (successMsg != null) {
            request.setAttribute("successMsg", successMsg);
            request.getSession().removeAttribute("successMsg");
        }
        if (errorMsg != null) {
            request.setAttribute("errorMsg", errorMsg);
            request.getSession().removeAttribute("errorMsg");
        }
        
        request.getRequestDispatcher("/view/dashboard/manager/maintenance/maintenance-list.jsp").forward(request, response);
    }
    
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Map<String, Object> result = new HashMap<>();
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "ID không hợp lệ");
            out.print(gson.toJson(result));
            return;
        }
        
        try {
            Maintenance m = maintenanceDAO.findById(Integer.parseInt(idStr));
            if (m != null) {
                result.put("success", true);
                result.put("maintenance", m);
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi: " + e.getMessage());
        }
        
        out.print(gson.toJson(result));
    }
    
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String machineIdStr = request.getParameter("machineId");
        String maintenanceType = request.getParameter("maintenanceType");
        String maintenanceDateStr = request.getParameter("maintenanceDate");
        String performedBy = request.getParameter("performedBy");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        
        if (machineIdStr == null || machineIdStr.isEmpty() ||
            maintenanceType == null || maintenanceType.isEmpty() ||
            maintenanceDateStr == null || maintenanceDateStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Vui lòng điền đầy đủ thông tin");
            response.sendRedirect(request.getContextPath() + "/manager/maintenances");
            return;
        }
        
        try {
            Maintenance m = Maintenance.builder()
                    .machineId(Integer.parseInt(machineIdStr))
                    .maintenanceType(maintenanceType)
                    .maintenanceDate(Date.valueOf(maintenanceDateStr))
                    .performedBy(performedBy != null ? performedBy.trim() : null)
                    .description(description != null ? description.trim() : null)
                    // Default: đang chờ
                    .status(status != null && !status.isEmpty() ? status : "PENDING")
                    .build();
            
            int newId = maintenanceDAO.insert(m);
            if (newId > 0) {
                // Đồng bộ trạng thái máy theo trạng thái bảo trì
                boolean machineUpdated = syncMachineStatusWithMaintenance(m.getMachineId(), m.getStatus());
                if (machineUpdated) {
                    request.getSession().setAttribute("successMsg", "Thêm bảo trì thành công.");
                } else {
                    request.getSession().setAttribute("successMsg", "Thêm bảo trì thành công. (Cảnh báo: không thể cập nhật trạng thái máy)");
                }
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể thêm bảo trì");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/maintenances");
    }
    
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        String machineIdStr = request.getParameter("machineId");
        String maintenanceType = request.getParameter("maintenanceType");
        String maintenanceDateStr = request.getParameter("maintenanceDate");
        String performedBy = request.getParameter("performedBy");
        String description = request.getParameter("description");
        String status = request.getParameter("status");
        
        if (idStr == null || machineIdStr == null || maintenanceType == null || maintenanceDateStr == null) {
            request.getSession().setAttribute("errorMsg", "Thông tin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/manager/maintenances");
            return;
        }
        
        try {
            // Không cho sửa nếu bảo trì đã hoàn thành (chặn cả backend để không lách UI)
            Maintenance existing = maintenanceDAO.findById(Integer.parseInt(idStr));
            if (existing != null && "COMPLETED".equalsIgnoreCase(existing.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Bảo trì đã hoàn thành nên không thể chỉnh sửa nữa.");
                response.sendRedirect(request.getContextPath() + "/manager/maintenances");
                return;
            }

            Maintenance m = Maintenance.builder()
                    .id(Integer.parseInt(idStr))
                    .machineId(Integer.parseInt(machineIdStr))
                    .maintenanceType(maintenanceType)
                    .maintenanceDate(Date.valueOf(maintenanceDateStr))
                    .performedBy(performedBy != null ? performedBy.trim() : null)
                    .description(description != null ? description.trim() : null)
                    .status(status)
                    .build();
            
            if (maintenanceDAO.update(m)) {
                // Đồng bộ trạng thái máy theo trạng thái bảo trì
                boolean machineUpdated = syncMachineStatusWithMaintenance(m.getMachineId(), m.getStatus());
                if (machineUpdated) {
                    request.getSession().setAttribute("successMsg", "Cập nhật thành công.");
                } else {
                    request.getSession().setAttribute("successMsg", "Cập nhật thành công. (Cảnh báo: không thể cập nhật trạng thái máy)");
                }
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể cập nhật");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/maintenances");
    }

    /**
     * PENDING (đang bảo trì) -> INACTIVE
     * COMPLETED (hoàn thành) -> ACTIVE
     * Other statuses -> keep machine INACTIVE by default (safe)
     */
    private boolean syncMachineStatusWithMaintenance(Integer machineId, String maintenanceStatus) {
        if (machineId == null) return false;
        String st = maintenanceStatus != null ? maintenanceStatus.trim().toUpperCase() : "PENDING";
        if ("COMPLETED".equals(st)) {
            return machineDAO.updateStatus(machineId, "ACTIVE");
        }
        // PENDING/CANCELLED/other -> mark inactive (not rentable)
        return machineDAO.deactivate(machineId);
    }
    
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "ID không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/manager/maintenances");
            return;
        }
        
        try {
            if (maintenanceDAO.delete(Integer.parseInt(idStr))) {
                request.getSession().setAttribute("successMsg", "Xóa thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể xóa");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/maintenances");
    }
}

