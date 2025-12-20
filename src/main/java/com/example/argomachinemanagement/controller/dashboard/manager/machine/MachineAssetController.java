package com.example.argomachinemanagement.controller.dashboard.manager.machine;

import com.example.argomachinemanagement.dal.MachineAssetDAO;
import com.example.argomachinemanagement.dal.MachineModelDAO;
import com.example.argomachinemanagement.entity.MachineAsset;
import com.example.argomachinemanagement.entity.MachineModel;
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

/**
 * Controller quản lý Machine Assets (Từng chiếc máy vật lý)
 * URL: /manager/machine-assets
 */
@WebServlet(name = "MachineAssetController", urlPatterns = {"/manager/machine-assets"})
public class MachineAssetController extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    private MachineAssetDAO machineAssetDAO;
    private MachineModelDAO machineModelDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        machineAssetDAO = new MachineAssetDAO();
        machineModelDAO = new MachineModelDAO();
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
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
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
            case "activate":
                handleActivate(request, response);
                break;
            case "setMaintenance":
                handleSetMaintenance(request, response);
                break;
            case "setAvailable":
                handleSetAvailable(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
        }
    }

    /**
     * Handle list with filters and pagination
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String modelIdStr = request.getParameter("modelId");
        String status = request.getParameter("status");
        String rentalStatus = request.getParameter("rentalStatus");
        String keyword = request.getParameter("keyword");

        Integer modelId = null;
        if (modelIdStr != null && !modelIdStr.isEmpty() && !modelIdStr.equals("All")) {
            try {
                modelId = Integer.parseInt(modelIdStr);
            } catch (NumberFormatException e) {
                // Ignore invalid modelId
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

        // Get filtered assets
        List<MachineAsset> allAssets = machineAssetDAO.findByFilters(modelId, status, rentalStatus, keyword);

        // Pagination logic
        int totalAssets = allAssets.size();
        int totalPages = (int) Math.ceil((double) totalAssets / PAGE_SIZE);

        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }

        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalAssets);

        List<MachineAsset> paginatedAssets = new ArrayList<>();
        if (startIndex < totalAssets) {
            paginatedAssets = allAssets.subList(startIndex, endIndex);
        }

        // Get data for dropdowns
        List<MachineModel> machineModels = machineModelDAO.findAll();

        // Set attributes for JSP
        request.setAttribute("assets", paginatedAssets);
        request.setAttribute("machineModels", machineModels);
        request.setAttribute("modelIdFilter", modelId);
        request.setAttribute("statusFilter", status != null ? status : "All");
        request.setAttribute("rentalStatusFilter", rentalStatus != null ? rentalStatus : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalAssets", totalAssets);
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
        request.getRequestDispatcher("/view/dashboard/manager/machine/machine-asset-list.jsp").forward(request, response);
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
            result.put("message", "Asset ID is required");
            out.print(gson.toJson(result));
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            MachineAsset asset = machineAssetDAO.findById(id);

            if (asset != null) {
                result.put("success", true);
                result.put("asset", asset);
            } else {
                result.put("success", false);
                result.put("message", "Asset not found");
            }
        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "Invalid asset ID");
        }

        out.print(gson.toJson(result));
    }

    /**
     * Handle create asset
     */
    private void handleCreate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serialNumber = request.getParameter("serialNumber");
        String modelIdStr = request.getParameter("modelId");
        String status = request.getParameter("status");
        String rentalStatus = request.getParameter("rentalStatus");
        String location = request.getParameter("location");
        String purchaseDateStr = request.getParameter("purchaseDate");
        String note = request.getParameter("note");

        // Validation
        if (serialNumber == null || serialNumber.trim().isEmpty() ||
            modelIdStr == null || modelIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Số serial và Dòng máy là bắt buộc");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        // Check if serial number exists
        if (machineAssetDAO.isSerialNumberExists(serialNumber.trim(), null)) {
            request.getSession().setAttribute("errorMsg", "Số serial đã tồn tại");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            MachineAsset asset = MachineAsset.builder()
                    .serialNumber(serialNumber.trim())
                    .modelId(Integer.parseInt(modelIdStr))
                    .status(status != null && !status.isEmpty() ? status : "ACTIVE")
                    .rentalStatus(rentalStatus != null && !rentalStatus.isEmpty() ? rentalStatus : "AVAILABLE")
                    .location(location != null ? location.trim() : null)
                    .purchaseDate(purchaseDateStr != null && !purchaseDateStr.isEmpty() ?
                            Date.valueOf(purchaseDateStr) : null)
                    .note(note != null ? note.trim() : null)
                    .build();

            int newId = machineAssetDAO.insert(asset);

            if (newId > 0) {
                request.getSession().setAttribute("successMsg", "Thêm máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Thêm máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    /**
     * Handle update asset
     */
    private void handleUpdate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String serialNumber = request.getParameter("serialNumber");
        String modelIdStr = request.getParameter("modelId");
        String status = request.getParameter("status");
        String rentalStatus = request.getParameter("rentalStatus");
        String location = request.getParameter("location");
        String purchaseDateStr = request.getParameter("purchaseDate");
        String note = request.getParameter("note");

        // Validation
        if (idStr == null || idStr.isEmpty() ||
            serialNumber == null || serialNumber.trim().isEmpty() ||
            modelIdStr == null || modelIdStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Tất cả các trường bắt buộc phải được điền");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Check if serial number exists (exclude current asset)
            if (machineAssetDAO.isSerialNumberExists(serialNumber.trim(), id)) {
                request.getSession().setAttribute("errorMsg", "Số serial đã tồn tại");
                response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
                return;
            }

            MachineAsset asset = MachineAsset.builder()
                    .id(id)
                    .serialNumber(serialNumber.trim())
                    .modelId(Integer.parseInt(modelIdStr))
                    .status(status != null && !status.isEmpty() ? status : "ACTIVE")
                    .rentalStatus(rentalStatus != null && !rentalStatus.isEmpty() ? rentalStatus : "AVAILABLE")
                    .location(location != null ? location.trim() : null)
                    .purchaseDate(purchaseDateStr != null && !purchaseDateStr.isEmpty() ?
                            Date.valueOf(purchaseDateStr) : null)
                    .note(note != null ? note.trim() : null)
                    .build();

            boolean success = machineAssetDAO.update(asset);

            if (success) {
                request.getSession().setAttribute("successMsg", "Cập nhật máy thành công");
            } else {
                request.getSession().setAttribute("errorMsg", "Cập nhật máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    /**
     * Handle deactivate asset (set status = INACTIVE)
     */
    private void handleDeactivate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Asset ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Kiểm tra xem máy có đang được thuê không
            MachineAsset asset = machineAssetDAO.findById(id);
            if (asset != null && "RENTED".equals(asset.getRentalStatus())) {
                request.getSession().setAttribute("errorMsg", "Không thể vô hiệu hóa máy đang được thuê");
                response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
                return;
            }

            boolean success = machineAssetDAO.updateStatus(id, "INACTIVE");

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã vô hiệu hóa máy");
            } else {
                request.getSession().setAttribute("errorMsg", "Vô hiệu hóa máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    /**
     * Handle activate asset (set status = ACTIVE)
     */
    private void handleActivate(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Asset ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);
            boolean success = machineAssetDAO.updateStatus(id, "ACTIVE");

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã kích hoạt máy");
            } else {
                request.getSession().setAttribute("errorMsg", "Kích hoạt máy thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    /**
     * Handle set maintenance (set rental_status = MAINTENANCE)
     */
    private void handleSetMaintenance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Asset ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Kiểm tra xem máy có đang được thuê không
            MachineAsset asset = machineAssetDAO.findById(id);
            if (asset != null && "RENTED".equals(asset.getRentalStatus())) {
                request.getSession().setAttribute("errorMsg", "Không thể chuyển sang bảo trì khi máy đang được thuê");
                response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
                return;
            }

            boolean success = machineAssetDAO.updateRentalStatus(id, "MAINTENANCE");

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã chuyển máy sang trạng thái bảo trì");
            } else {
                request.getSession().setAttribute("errorMsg", "Chuyển trạng thái thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    /**
     * Handle set available (set rental_status = AVAILABLE)
     */
    private void handleSetAvailable(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.isEmpty()) {
            request.getSession().setAttribute("errorMsg", "Asset ID is required");
            response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            // Kiểm tra xem máy có đang ACTIVE không
            MachineAsset asset = machineAssetDAO.findById(id);
            if (asset != null && !"ACTIVE".equals(asset.getStatus())) {
                request.getSession().setAttribute("errorMsg", "Chỉ có thể đặt sẵn sàng cho máy đang hoạt động");
                response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
                return;
            }

            boolean success = machineAssetDAO.updateRentalStatus(id, "AVAILABLE");

            if (success) {
                request.getSession().setAttribute("successMsg", "Đã chuyển máy sang trạng thái sẵn sàng");
            } else {
                request.getSession().setAttribute("errorMsg", "Chuyển trạng thái thất bại");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/manager/machine-assets");
    }

    @Override
    public String getServletInfo() {
        return "Machine Asset Controller - Manage individual machines";
    }
}
