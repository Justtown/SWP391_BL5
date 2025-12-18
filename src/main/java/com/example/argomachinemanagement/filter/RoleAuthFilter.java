package com.example.argomachinemanagement.filter;

import com.example.argomachinemanagement.dal.PermissionDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Filter kiểm tra quyền truy cập URL dựa trên permissions trong database
 * - Lấy danh sách URL patterns được phép từ session (đã load khi login)
 * - Kiểm tra request URL có nằm trong danh sách được phép không
 */
@WebFilter(filterName = "RoleAuthFilter", urlPatterns = {"/admin/*", "/manager/*", "/sale/*", "/customer/*"})
public class RoleAuthFilter implements Filter {
    
    private PermissionDAO permissionDAO;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        permissionDAO = new PermissionDAO();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        // Kiểm tra đã đăng nhập chưa
        if (session == null || session.getAttribute("user") == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // Lấy userId và roleName từ session
        Integer userId = (Integer) session.getAttribute("userId");
        String roleName = (String) session.getAttribute("roleName");
        if (userId == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }
        
        // Nếu là admin thì cho phép truy cập tất cả các URL /admin/* (bỏ qua kiểm tra permission chi tiết)
        if (roleName != null && roleName.equalsIgnoreCase("admin")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Lấy request path
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = requestURI.substring(contextPath.length());
        
        // Lấy danh sách URL patterns được phép từ session
        @SuppressWarnings("unchecked")
        Set<String> allowedUrls = (Set<String>) session.getAttribute("allowedUrls");
        
        // Nếu chưa có trong session, load từ database
        if (allowedUrls == null) {
            allowedUrls = permissionDAO.getAllowedUrlPatternsByUserId(userId);
        }

        // Bổ sung một số URL mặc định theo role (không cần sửa DB)
        if (roleName != null && allowedUrls != null) {
            if (roleName.equalsIgnoreCase("customer")) {
                // Cho phép customer truy cập trang danh sách hợp đồng riêng
                allowedUrls.add("/customer/contracts");
            }
            if (roleName.equalsIgnoreCase("manager")) {
                // Cho phép manager truy cập trang quản lý đơn hàng
                allowedUrls.add("/manager/orders");
            }
        }

        // Lưu lại vào session (phòng khi có thay đổi ở trên)
        session.setAttribute("allowedUrls", allowedUrls);
        
        // Kiểm tra quyền truy cập
        if (!hasPermission(path, allowedUrls)) {
            // Không có quyền truy cập
            httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Bạn không có quyền truy cập trang này: " + path);
            return;
        }
        
        // Có quyền, cho phép tiếp tục
        chain.doFilter(request, response);
    }
    
    /**
     * Kiểm tra URL có được phép truy cập không
     * @param requestPath URL path cần kiểm tra
     * @param allowedUrls Danh sách URL patterns được phép
     * @return true nếu được phép
     */
    private boolean hasPermission(String requestPath, Set<String> allowedUrls) {
        if (allowedUrls == null || allowedUrls.isEmpty()) {
            return false;
        }
        
        for (String pattern : allowedUrls) {
            if (matchesUrl(requestPath, pattern)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Kiểm tra URL có khớp với pattern không
     * Hỗ trợ matching cho URL và các sub-paths
     */
    private boolean matchesUrl(String requestPath, String pattern) {
        if (pattern == null || requestPath == null) {
            return false;
        }
        
        // Exact match
        if (requestPath.equals(pattern)) {
            return true;
        }
        
        // StartsWith match (cho phép sub-paths và query params)
        // VD: /manager/machines/add được phép nếu có quyền /manager/machines
        if (requestPath.startsWith(pattern + "/") || requestPath.startsWith(pattern + "?")) {
            return true;
        }
        
        return false;
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
