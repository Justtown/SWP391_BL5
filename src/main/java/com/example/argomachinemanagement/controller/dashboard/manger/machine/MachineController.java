package com.example.argomachinemanagement.controller.dashboard.manger.machine;

import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.entity.Machine;
import com.example.argomachinemanagement.entity.MachineType;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MachineController", urlPatterns = {"/manager/machines"})
public class MachineController extends HttpServlet {
    
    private static final int PAGE_SIZE = 5;
    
    private MachineDAO machineDAO;
    private MachineTypeDAO machineTypeDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        machineDAO = new MachineDAO();
        machineTypeDAO = new MachineTypeDAO();
        gson = new Gson();
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
            response.sendRedirect(request.getContextPath() + "/manager/machines");
            return;
        }
        
        switch (action) {
            case "create":
                handleCreate(request, response);
                break;
            case "update":
                handleUpdate(request, response);
                break;
            case "deactivate":
                handleDeactivate(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/machines");
        }
    }
    
    /**
     * Handle list with filters and pagination
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String statusFilter = request.getParameter("status");
        String keyword = request.getParameter("keyword");
        String typeIdStr = request.getParameter("typeId");
        Integer typeId = null;
        if (typeIdStr != null && !typeIdStr.isEmpty() && !typeIdStr.equals("All Types")) {
            try {
                typeId = Integer.parseInt(typeIdStr);
            } catch (NumberFormatException e) {
                // Ignore invalid typeId
            }
        }
        
        // Get pagination parameter
        String pageStr = request.getParameter("page");
        int currentPage = 1;
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) {
                    currentPage = 1;
                }
            } catch (NumberFormatException e) {
                currentPage = 1;
            }
        }
        
        // Get filtered machines
        List<Machine> allMachines = machineDAO.findByFilters(statusFilter, keyword, typeId);
        
        // Pagination logic
        int totalMachines = allMachines.size();
        int totalPages = (int) Math.ceil((double) totalMachines / PAGE_SIZE);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalMachines);
        
        List<Machine> paginatedMachines = new ArrayList<>();
        if (startIndex < totalMachines) {
            paginatedMachines = allMachines.subList(startIndex, endIndex);
        }
        
        // Get machine types for dropdown
        List<MachineType> machineTypes = machineTypeDAO.findAll();
        
        // Set attributes for JSP
        request.setAttribute("machines", paginatedMachines);
        request.setAttribute("machineTypes", machineTypes);
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "All Status");
        request.setAttribute("typeIdFilter", typeId);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalMachines", totalMachines);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("startIndex", startIndex);
        
        // Check for success/error messages from session
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
        
        // Forward to JSP
        request.getRequestDispatcher("/view/dashboard/manager/machine/machine-list.jsp").forward(request, response);
    }
    
    /**
     * Handle detail request (returns JSON for modal)
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Map<String, Object> result = new HashMap<>();
        
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "Machine ID is required");
            out.print(gson.toJson(result));
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            Machine machine = machineDAO.findById(id);
            
            if (machine != null) {
                result.put("success", true);
                result.put("machine", machine);
            } else {
                result.put("success", false);
                result.put("message", "Machine not found");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid machine ID");
        }
        
        out.print(gson.toJson(result));
    }
    
    /**
     * Handle create machine
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String machineCode = request.getParameter("machineCode");
        String machineName = request.getParameter("machineName");
        String machineTypeIdStr = request.getParameter("machineTypeId");
        String status = request.getParameter("status");
        String isRentableStr = request.getParameter("isRentable");
        String location = request.getParameter("location");
        String purchaseDateStr = request.getParameter("purchaseDate");
        String description = request.getParameter("description");
        
        // Validation
        if (machineCode == null || machineCode.trim().isEmpty() ||
            machineName == null || machineName.trim().isEmpty() ||
            machineTypeIdStr == null || machineTypeIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Machine Code, Name and Type are required");
            response.sendRedirect(request.getContextPath() + "/manager/machines");
            return;
        }
        
        // Check if machine code exists
        if (machineDAO.isMachineCodeExists(machineCode.trim(), null)) {
            request.getSession().setAttribute("errorMsg", "Machine Code already exists");
            response.sendRedirect(request.getContextPath() + "/manager/machines");
            return;
        }
        
        try {
            Machine machine = Machine.builder()
                    .machineCode(machineCode.trim())
                    .machineName(machineName.trim())
                    .machineTypeId(Integer.parseInt(machineTypeIdStr))
                    .status(status != null && !status.isEmpty() ? status : "ACTIVE")
                    .isRentable(isRentableStr != null && isRentableStr.equals("on"))
                    .location(location != null ? location.trim() : null)
                    .purchaseDate(purchaseDateStr != null && !purchaseDateStr.isEmpty() ? 
                            Date.valueOf(purchaseDateStr) : null)
                    .description(description != null ? description.trim() : null)
                    .build();
            
            int newId = machineDAO.insert(machine);
            
            if (newId > 0) {
                request.getSession().setAttribute("successMsg", "Machine created successfully");
            } else {
                request.getSession().setAttribute("errorMsg", "Failed to create machine");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error creating machine: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machines");
    }
    
    /**
     * Handle update machine
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String machineCode = request.getParameter("machineCode");
        String machineName = request.getParameter("machineName");
        String machineTypeIdStr = request.getParameter("machineTypeId");
        String status = request.getParameter("status");
        String isRentableStr = request.getParameter("isRentable");
        String location = request.getParameter("location");
        String purchaseDateStr = request.getParameter("purchaseDate");
        String description = request.getParameter("description");
        
        // Validation
        if (idStr == null || idStr.isEmpty() ||
            machineCode == null || machineCode.trim().isEmpty() ||
            machineName == null || machineName.trim().isEmpty() ||
            machineTypeIdStr == null || machineTypeIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "All required fields must be filled");
            response.sendRedirect(request.getContextPath() + "/manager/machines");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            
            // Check if machine code exists (exclude current machine)
            if (machineDAO.isMachineCodeExists(machineCode.trim(), id)) {
                request.getSession().setAttribute("errorMsg", "Machine Code already exists");
                response.sendRedirect(request.getContextPath() + "/manager/machines");
                return;
            }
            
            Machine machine = Machine.builder()
                    .id(id)
                    .machineCode(machineCode.trim())
                    .machineName(machineName.trim())
                    .machineTypeId(Integer.parseInt(machineTypeIdStr))
                    .status(status != null && !status.isEmpty() ? status : "ACTIVE")
                    .isRentable(isRentableStr != null && isRentableStr.equals("on"))
                    .location(location != null ? location.trim() : null)
                    .purchaseDate(purchaseDateStr != null && !purchaseDateStr.isEmpty() ? 
                            Date.valueOf(purchaseDateStr) : null)
                    .description(description != null ? description.trim() : null)
                    .build();
            
            boolean success = machineDAO.update(machine);
            
            if (success) {
                request.getSession().setAttribute("successMsg", "Machine updated successfully");
            } else {
                request.getSession().setAttribute("errorMsg", "Failed to update machine");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error updating machine: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machines");
    }
    
    /**
     * Handle deactivate machine
     */
    private void handleDeactivate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Machine ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machines");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            boolean success = machineDAO.deactivate(id);
            
            if (success) {
                request.getSession().setAttribute("successMsg", "Machine deactivated successfully");
            } else {
                request.getSession().setAttribute("errorMsg", "Failed to deactivate machine");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Error deactivating machine: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machines");
    }
}

