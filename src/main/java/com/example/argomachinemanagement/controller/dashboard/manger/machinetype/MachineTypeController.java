package com.example.argomachinemanagement.controller.dashboard.manger.machinetype;

import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.dal.MachineDAO;
import com.example.argomachinemanagement.entity.MachineType;
import com.example.argomachinemanagement.entity.Machine;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MachineTypeController", urlPatterns = {"/manager/machine-types"})
public class MachineTypeController extends HttpServlet {
    
    private MachineTypeDAO machineTypeDAO;
    private MachineDAO machineDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        machineTypeDAO = new MachineTypeDAO();
        machineDAO = new MachineDAO();
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
        } else if (action.equals("machines")) {
            handleMachinesByType(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager/machine-types");
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
                response.sendRedirect(request.getContextPath() + "/manager/machine-types");
        }
    }
    
    /**
     * Handle list machine types
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        
        List<MachineType> types;
        if (keyword != null && !keyword.trim().isEmpty()) {
            types = machineTypeDAO.search(keyword.trim());
        } else {
            types = machineTypeDAO.findAll();
        }
        
        // Get machine count for each type
        Map<Integer, Integer> machineCounts = new HashMap<>();
        int totalMachines = 0;
        for (MachineType type : types) {
            int count = machineTypeDAO.countMachinesByType(type.getId());
            machineCounts.put(type.getId(), count);
            totalMachines += count;
        }
        
        // Get statistics
        List<Machine> allMachines = machineDAO.findAll();
        int activeMachines = 0;
        int inactiveMachines = 0;
        for (Machine machine : allMachines) {
            if ("ACTIVE".equals(machine.getStatus())) {
                activeMachines++;
            } else {
                inactiveMachines++;
            }
        }
        
        request.setAttribute("machineTypes", types);
        request.setAttribute("machineCounts", machineCounts);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        
        // Statistics
        request.setAttribute("totalTypes", types.size());
        request.setAttribute("totalMachines", allMachines.size());
        request.setAttribute("activeMachines", activeMachines);
        request.setAttribute("inactiveMachines", inactiveMachines);
        
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
        
        request.getRequestDispatcher("/view/dashboard/manager/machinetype/machine-type-list.jsp").forward(request, response);
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
            result.put("message", "Machine Type ID is required");
            out.print(gson.toJson(result));
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            MachineType type = machineTypeDAO.findById(id);
            
            if (type != null) {
                result.put("success", true);
                result.put("machineType", type);
                result.put("machineCount", machineTypeDAO.countMachinesByType(id));
            } else {
                result.put("success", false);
                result.put("message", "Machine Type not found");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid Machine Type ID");
        }
        
        out.print(gson.toJson(result));
    }
    
    /**
     * Handle get machines by type (returns JSON)
     */
    private void handleMachinesByType(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Map<String, Object> result = new HashMap<>();
        
        String typeIdStr = request.getParameter("typeId");
        if (typeIdStr == null || typeIdStr.isEmpty()) {
            result.put("success", false);
            result.put("message", "Type ID is required");
            out.print(gson.toJson(result));
            return;
        }
        
        try {
            int typeId = Integer.parseInt(typeIdStr);
            List<Machine> machines = machineDAO.findByFilters(null, null, typeId);
            result.put("success", true);
            result.put("machines", machines);
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid Type ID");
        }
        
        out.print(gson.toJson(result));
    }
    
    /**
     * Handle create machine type
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String typeName = request.getParameter("typeName");
        String description = request.getParameter("description");
        
        // Validation
        if (typeName == null || typeName.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Tên loại máy không được để trống");
            response.sendRedirect(request.getContextPath() + "/manager/machine-types");
            return;
        }
        
        // Check if type name exists
        if (machineTypeDAO.isTypeNameExists(typeName.trim(), null)) {
            request.getSession().setAttribute("errorMsg", "Tên loại máy đã tồn tại");
            response.sendRedirect(request.getContextPath() + "/manager/machine-types");
            return;
        }
        
        try {
            MachineType type = MachineType.builder()
                    .typeName(typeName.trim())
                    .description(description != null ? description.trim() : null)
                    .build();
            
            int newId = machineTypeDAO.insert(type);
            
            if (newId > 0) {
                request.getSession().setAttribute("successMsg", "Thêm loại máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể thêm loại máy");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machine-types");
    }
    
    /**
     * Handle update machine type
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String typeName = request.getParameter("typeName");
        String description = request.getParameter("description");
        
        // Validation
        if (idStr == null || idStr.isEmpty() || typeName == null || typeName.trim().isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Thông tin không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/manager/machine-types");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            
            // Check if type name exists (exclude current)
            if (machineTypeDAO.isTypeNameExists(typeName.trim(), id)) {
                request.getSession().setAttribute("errorMsg", "Tên loại máy đã tồn tại");
                response.sendRedirect(request.getContextPath() + "/manager/machine-types");
                return;
            }
            
            MachineType type = MachineType.builder()
                    .id(id)
                    .typeName(typeName.trim())
                    .description(description != null ? description.trim() : null)
                    .build();
            
            boolean success = machineTypeDAO.update(type);
            
            if (success) {
                request.getSession().setAttribute("successMsg", "Cập nhật loại máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể cập nhật loại máy");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machine-types");
    }
    
    /**
     * Handle delete machine type
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        
        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "ID loại máy không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/manager/machine-types");
            return;
        }
        
        try {
            int id = Integer.parseInt(idStr);
            
            // Check if any machines are using this type
            int machineCount = machineTypeDAO.countMachinesByType(id);
            if (machineCount > 0) {
                request.getSession().setAttribute("errorMsg", 
                    "Không thể xóa loại máy này vì còn " + machineCount + " máy đang sử dụng");
                response.sendRedirect(request.getContextPath() + "/manager/machine-types");
                return;
            }
            
            boolean success = machineTypeDAO.delete(id);
            
            if (success) {
                request.getSession().setAttribute("successMsg", "Xóa loại máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Không thể xóa loại máy");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        
        response.sendRedirect(request.getContextPath() + "/manager/machine-types");
    }
}

