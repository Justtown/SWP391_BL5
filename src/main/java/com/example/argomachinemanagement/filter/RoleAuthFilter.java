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
            "/customer/contracts", "/contracts",
            // Manager routes mới nhưng permission DB có thể đang lưu route cũ
            "/manager/machine-types", "/machine-types",
            "/manager/maintenances", "/maintenances"
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
        if (requestPath == null || allowedUrls == null || allowedUrls.isEmpty()) {
            return false;
        }

        // Hỗ trợ cả trường hợp sub-path: /new/foo/bar -> /legacy/foo/bar
        for (Map.Entry<String, String> entry : LEGACY_PERMISSION_ALIASES.entrySet()) {
            String newBase = entry.getKey();
            String legacyBase = entry.getValue();

            if (matchesUrl(requestPath, newBase)) {
                // replace prefix nếu có phần đuôi path
                String legacyPath = requestPath;
                if (legacyPath.startsWith(newBase)) {
                    legacyPath = legacyBase + legacyPath.substring(newBase.length());
                } else {
                    // fallback nếu matchUrl do normalize/wildcard
                    legacyPath = legacyBase;
                }
                if (hasPermission(legacyPath, allowedUrls)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Một số URL quan trọng được cho phép mặc định theo role
     * để tránh lỗi 403 nếu chưa cấu hình permission trong DB.
     */
    private boolean hasRoleBasedDefaultPermission(String requestPath, String roleName) {
        if (roleName == null || requestPath == null) {
            return false;
        }

        switch (roleName.toLowerCase()) {
            case "admin":
                // Admin mặc định được phép truy cập toàn bộ khu vực /admin/*
                // để tránh lỗi 403 nếu permission trong DB chưa cấu hình đầy đủ.
                return requestPath.startsWith("/admin/")
                        || requestPath.startsWith("/admin/pending-users")
                        || requestPath.startsWith("/admin/manage-account")
                        || requestPath.startsWith("/admin/add-user")
                        || requestPath.startsWith("/admin/role-management");
            case "manager":
                // Manager mặc định được vào các trang nghiệp vụ cốt lõi để tránh 403
                // khi DB chưa kịp cấu hình permission cho route mới
                return requestPath.startsWith("/manager/machine-types")
                        || requestPath.startsWith("/manager/maintenances");
                // Admin luôn được vào một số trang quản trị quan trọng
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

        // Normalize: ensure leading slash & remove trailing slash (except root)
        String normalizedRequest = requestPath.startsWith("/") ? requestPath : "/" + requestPath;
        String normalizedPattern = pattern.startsWith("/") ? pattern : "/" + pattern;
        if (normalizedPattern.length() > 1 && normalizedPattern.endsWith("/")) {
            normalizedPattern = normalizedPattern.substring(0, normalizedPattern.length() - 1);
        }

        // Support wildcard patterns commonly stored in DB: "/foo/*" or "/foo*"
        String basePattern = normalizedPattern;
        if (normalizedPattern.endsWith("/*")) {
            basePattern = normalizedPattern.substring(0, normalizedPattern.length() - 2);
        } else if (normalizedPattern.endsWith("*")) {
            basePattern = normalizedPattern.substring(0, normalizedPattern.length() - 1);
        }
        if (basePattern.length() > 1 && basePattern.endsWith("/")) {
            basePattern = basePattern.substring(0, basePattern.length() - 1);
        }

        // Exact match (also allow wildcard base match)
        if (normalizedRequest.equals(normalizedPattern) || normalizedRequest.equals(basePattern)) {
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
