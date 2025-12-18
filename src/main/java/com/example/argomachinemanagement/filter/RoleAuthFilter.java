package com.example.argomachinemanagement.filter;

import com.example.argomachinemanagement.dal.PermissionDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Filter kiểm tra quyền truy cập URL dựa trên permissions trong database
 * - Lấy danh sách URL patterns được phép từ session (đã load khi login)
 * - Kiểm tra request URL có nằm trong danh sách được phép không
 */
@WebFilter(filterName = "RoleAuthFilter", urlPatterns = {"/admin/*", "/manager/*", "/sale/*", "/customer/*"})
public class RoleAuthFilter implements Filter {
    
    private PermissionDAO permissionDAO;
    /**
     * Map các URL mới -> URL cũ để tương thích permission đã lưu trong DB.
     * Ví dụ: DB đang có /contracts nhưng UI/route mới là /manager/contracts.
     */
    private static final Map<String, String> LEGACY_PERMISSION_ALIASES = Map.of(
            "/admin/manage-account", "/manage-account",
            "/admin/add-user", "/add-user",
            "/admin/pending-users", "/pending-users",
            "/manager/contracts", "/contracts",
            "/sale/contracts", "/contracts",
            "/customer/contracts", "/contracts"
    );

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
        
        // Lấy userId từ session
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        // ====== CHẶN ROLE BỊ DEACTIVE ======
        Integer roleStatus = (Integer) session.getAttribute("roleStatus");
        if (roleStatus != null && roleStatus == 0) {
            session.invalidate();
            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                            + "/login?error=role_inactive"
            );
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
            session.setAttribute("allowedUrls", allowedUrls);
        }
        
        String roleName = (String) session.getAttribute("roleName");
        
        // Kiểm tra quyền truy cập
        if (!hasPermission(path, allowedUrls)
                && !hasLegacyAliasPermission(path, allowedUrls)
                && !hasRoleBasedDefaultPermission(path, roleName)) {
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
     * Kiểm tra quyền thông qua alias (URL mới -> URL cũ).
     * Chỉ áp dụng cho một số URL đã được map cố định trong LEGACY_PERMISSION_ALIASES.
     */
    private boolean hasLegacyAliasPermission(String requestPath, Set<String> allowedUrls) {
        String legacy = LEGACY_PERMISSION_ALIASES.get(requestPath);
        if (legacy == null) {
            return false;
        }
        return hasPermission(legacy, allowedUrls);
    }

    /**
     * Một số URL quan trọng được cho phép mặc định theo role
     * để tránh lỗi 403 nếu chưa cấu hình permission trong DB.
     */
    private boolean hasRoleBasedDefaultPermission(String requestPath, String roleName) {
        if (roleName == null || requestPath == null) {
            return false;
        }

        switch (roleName) {
            case "admin":
                // Admin luôn được vào một số trang quản trị quan trọng
                return requestPath.startsWith("/admin/pending-users")
                        || requestPath.startsWith("/admin/manage-account")
                        || requestPath.startsWith("/admin/add-user")
                        || requestPath.startsWith("/admin/role-management");
            case "customer":
                // Customer luôn được xem hợp đồng của mình
                return requestPath.startsWith("/customer/contracts");
            default:
                return false;
        }
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
