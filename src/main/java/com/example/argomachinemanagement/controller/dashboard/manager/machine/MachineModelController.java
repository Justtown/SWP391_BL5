package com.example.argomachinemanagement.controller.dashboard.manager.machine;

import com.example.argomachinemanagement.dal.MachineModelDAO;
import com.example.argomachinemanagement.dal.MachineTypeDAO;
import com.example.argomachinemanagement.entity.MachineModel;
import com.example.argomachinemanagement.entity.MachineType;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Machine Models (Dòng máy)
 * URL: /manager/machine-models
 */
@WebServlet(name = "MachineModelController", urlPatterns = {"/manager/machine-models"})
public class MachineModelController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private MachineModelDAO machineModelDAO;
    private MachineTypeDAO machineTypeDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        machineModelDAO = new MachineModelDAO();
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
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/manager/machine-models");
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
                response.sendRedirect(request.getContextPath() + "/manager/machine-models");
        }
    }

    /**
     * Handle list with filters and pagination
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String typeIdStr = request.getParameter("typeId");
        String brand = request.getParameter("brand");
        String keyword = request.getParameter("keyword");

        Integer typeId = null;
        if (typeIdStr != null && !typeIdStr.isEmpty() && !typeIdStr.equals("All")) {
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

        // Get filtered models
        List<MachineModel> allModels = machineModelDAO.findByFilters(typeId, brand, keyword);

        // Pagination logic
        int totalModels = allModels.size();
        int totalPages = (int) Math.ceil((double) totalModels / PAGE_SIZE);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalModels);

        List<MachineModel> paginatedModels = new ArrayList<>();
        if (startIndex < totalModels) {
            paginatedModels = allModels.subList(startIndex, endIndex);
        }

        // Get data for dropdowns
        List<MachineType> machineTypes = machineTypeDAO.findAll();
        List<String> brands = machineModelDAO.getAllBrands();

        // Set attributes for JSP
        request.setAttribute("models", paginatedModels);
        request.setAttribute("machineTypes", machineTypes);
        request.setAttribute("brands", brands);
        request.setAttribute("typeIdFilter", typeId);
        request.setAttribute("brandFilter", brand != null ? brand : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalModels", totalModels);
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
        request.getRequestDispatcher("/view/dashboard/manager/machine/machine-model-list.jsp").forward(request, response);
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
            result.put("message", "Model ID is required");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            MachineModel model = machineModelDAO.findById(id);

            if (model != null) {
                result.put("success", true);
                result.put("model", model);
                // Thêm số lượng asset liên kết
                result.put("assetCount", machineModelDAO.countAssetsByModel(id));
            } else {
                result.put("success", false);
                result.put("message", "Model not found");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid model ID");
        }

        out.print(gson.toJson(result));
    }

    /**
     * Handle create model
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String modelCode = request.getParameter("modelCode");
        String modelName = request.getParameter("modelName");
        String brand = request.getParameter("brand");
        String typeIdStr = request.getParameter("typeId");
        String specs = request.getParameter("specs");

        // Validation
        if (modelCode == null || modelCode.trim().isEmpty() ||
            modelName == null || modelName.trim().isEmpty() ||
            brand == null || brand.trim().isEmpty() ||
            typeIdStr == null || typeIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Mã dòng máy, Tên, Thương hiệu và Loại máy là bắt buộc");
            response.sendRedirect(request.getContextPath() + "/manager/machine-models");
            return;
        }

        // Check if model code exists
        if (machineModelDAO.isModelCodeExists(modelCode.trim(), null)) {
            request.getSession().setAttribute("errorMsg", "Mã dòng máy đã tồn tại");
            response.sendRedirect(request.getContextPath() + "/manager/machine-models");
            return;
        }

        try {
            MachineModel model = MachineModel.builder()
                    .modelCode(modelCode.trim())
                    .modelName(modelName.trim())
                    .brand(brand.trim())
                    .typeId(Integer.parseInt(typeIdStr))
                    .specs(specs != null ? specs.trim() : null)
                    .build();

            int newId = machineModelDAO.insert(model);

            if (newId > 0) {
                request.getSession().setAttribute("successMsg", "Tạo dòng máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Tạo dòng máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-models");
    }

    /**
     * Handle update model
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String modelCode = request.getParameter("modelCode");
        String modelName = request.getParameter("modelName");
        String brand = request.getParameter("brand");
        String typeIdStr = request.getParameter("typeId");
        String specs = request.getParameter("specs");

        // Validation
        if (idStr == null || idStr.isEmpty() ||
            modelCode == null || modelCode.trim().isEmpty() ||
            modelName == null || modelName.trim().isEmpty() ||
            brand == null || brand.trim().isEmpty() ||
            typeIdStr == null || typeIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Tất cả các trường bắt buộc phải được điền");
            response.sendRedirect(request.getContextPath() + "/manager/machine-models");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Check if model code exists (exclude current model)
            if (machineModelDAO.isModelCodeExists(modelCode.trim(), id)) {
                request.getSession().setAttribute("errorMsg", "Mã dòng máy đã tồn tại");
                response.sendRedirect(request.getContextPath() + "/manager/machine-models");
                return;
            }

            MachineModel model = MachineModel.builder()
                    .id(id)
                    .modelCode(modelCode.trim())
                    .modelName(modelName.trim())
                    .brand(brand.trim())
                    .typeId(Integer.parseInt(typeIdStr))
                    .specs(specs != null ? specs.trim() : null)
                    .build();

            boolean success = machineModelDAO.update(model);

            if (success) {
                request.getSession().setAttribute("successMsg", "Cập nhật dòng máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Cập nhật dòng máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-models");
    }

    /**
     * Handle delete model (kiểm tra ràng buộc FK trước)
     */
    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Model ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machine-models");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Kiểm tra xem có asset nào liên kết không
            int assetCount = machineModelDAO.countAssetsByModel(id);
            if (assetCount > 0) {
                request.getSession().setAttribute("errorMsg",
                    "Không thể xóa dòng máy này vì có " + assetCount + " máy đang liên kết. Hãy xóa hoặc chuyển các máy trước.");
                response.sendRedirect(request.getContextPath() + "/manager/machine-models");
                return;
            }

            boolean success = machineModelDAO.deleteById(id);

            if (success) {
                request.getSession().setAttribute("successMsg", "Xóa dòng máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Xóa dòng máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-models");
    }

    @Override
    public String getServletInfo() {
        return "Machine Model Controller - Manage machine models/series";
    }
}
