package com.example.argomachinemanagement.controller.product;

import com.example.argomachinemanagement.dal.ContractItemDAO;
import com.example.argomachinemanagement.entity.ContractItem;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "CustomerProductController", urlPatterns = {"/customer/products"})
public class CustomerProductController extends HttpServlet {
    
    private static final int PAGE_SIZE = 10;
    
    private ContractItemDAO contractItemDAO;
    
    @Override
    public void init() throws ServletException {
        contractItemDAO = new ContractItemDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String roleName = (String) session.getAttribute("roleName");
        if (roleName == null || !"customer".equalsIgnoreCase(roleName)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập trang này");
            return;
        }
        
        String action = request.getParameter("action");
        
        if (action == null || action.equals("list")) {
            handleList(request, response);
        } else if ("detail".equals(action)) {
            handleDetail(request, response);
        }
    }
    
    /**
     * Xử lý danh sách sản phẩm (máy) của customer
     */
    private void handleList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        
        // Get filter parameters
        String statusFilter = request.getParameter("status"); // ACTIVE, FINISHED, All
        String keyword = request.getParameter("keyword");
        
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
        
        // Get all machines for this customer
        List<ContractItem> allItems = contractItemDAO.findByCustomerId(userId);
        
        // Apply filters
        List<ContractItem> filteredItems = new ArrayList<>(allItems);
        
        // Filter by status (ACTIVE = đang thuê, FINISHED = đã thuê)
        if (statusFilter != null && !statusFilter.isEmpty() && !"All".equals(statusFilter)) {
            filteredItems = filteredItems.stream()
                .filter(item -> statusFilter.equals(item.getContractStatus()))
                .collect(Collectors.toList());
        }
        
        // Filter by keyword (search in serial number, model name, brand, contract code)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.toLowerCase().trim();
            filteredItems = filteredItems.stream()
                .filter(item -> 
                    (item.getSerialNumber() != null && item.getSerialNumber().toLowerCase().contains(keywordLower)) ||
                    (item.getModelName() != null && item.getModelName().toLowerCase().contains(keywordLower)) ||
                    (item.getBrand() != null && item.getBrand().toLowerCase().contains(keywordLower)) ||
                    (item.getContractCode() != null && item.getContractCode().toLowerCase().contains(keywordLower))
                )
                .collect(Collectors.toList());
        }
        
        // Pagination logic
        int totalItems = filteredItems.size();
        int totalPages = (int) Math.ceil((double) totalItems / PAGE_SIZE);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalItems);
        
        List<ContractItem> paginatedItems = new ArrayList<>();
        if (startIndex < totalItems) {
            paginatedItems = filteredItems.subList(startIndex, endIndex);
        }
        
        // Set attributes for JSP
        request.setAttribute("products", paginatedItems);
        request.setAttribute("statusFilter", statusFilter != null ? statusFilter : "All");
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalItems", totalItems);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("startIndex", startIndex);
        
        // Count by status
        long activeCount = allItems.stream().filter(item -> "ACTIVE".equals(item.getContractStatus())).count();
        long finishedCount = allItems.stream().filter(item -> "FINISHED".equals(item.getContractStatus())).count();
        request.setAttribute("activeCount", activeCount);
        request.setAttribute("finishedCount", finishedCount);
        
        // Forward to JSP
        request.getRequestDispatcher("/view/dashboard/customer/product-list.jsp").forward(request, response);
    }
    
    /**
     * Xử lý xem chi tiết sản phẩm (máy)
     */
    private void handleDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        
        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/customer/products?error=invalid");
            return;
        }
        
        try {
            int itemId = Integer.parseInt(idStr);
            
            // Get all items for this customer
            List<ContractItem> customerItems = contractItemDAO.findByCustomerId(userId);
            
            // Find the specific item
            ContractItem item = customerItems.stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);
            
            if (item == null) {
                response.sendRedirect(request.getContextPath() + "/customer/products?error=notfound");
                return;
            }
            
            request.setAttribute("product", item);
            request.getRequestDispatcher("/view/dashboard/customer/product-detail.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/customer/products?error=invalid");
        } catch (Exception e) {
            System.out.println("[CustomerProductController] Error in handleDetail: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error loading product details: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
