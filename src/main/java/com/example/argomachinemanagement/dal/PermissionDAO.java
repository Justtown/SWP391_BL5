package com.example.argomachinemanagement.dal;

import com.example.argomachinemanagement.entity.Permission;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DAO để quản lý permissions
 */
public class PermissionDAO extends DBContext {

    /**
     * Lấy tất cả permissions của một user dựa trên user_id
     * @param userId ID của user
     * @return Danh sách Permission
     */
    public List<Permission> getPermissionsByUserId(int userId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT DISTINCT p.* " +
                     "FROM permissions p " +
                     "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                     "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
                     "WHERE ur.user_id = ?";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Permission p = Permission.builder()
                        .id(resultSet.getInt("id"))
                        .permissionName(resultSet.getString("permission_name"))
                        .description(resultSet.getString("description"))
                        .urlPattern(resultSet.getString("url_pattern"))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .build();
                permissions.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Error in getPermissionsByUserId: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return permissions;
    }

    /**
     * Lấy tất cả URL patterns mà user được phép truy cập
     * @param userId ID của user
     * @return Set các URL patterns
     */
    public Set<String> getAllowedUrlPatternsByUserId(int userId) {
        Set<String> urlPatterns = new HashSet<>();
        String sql = "SELECT DISTINCT p.url_pattern " +
                     "FROM permissions p " +
                     "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                     "INNER JOIN user_role ur ON rp.role_id = ur.role_id " +
                     "WHERE ur.user_id = ? AND p.url_pattern IS NOT NULL";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String urlPattern = resultSet.getString("url_pattern");
                if (urlPattern != null && !urlPattern.isEmpty()) {
                    urlPatterns.add(urlPattern);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getAllowedUrlPatternsByUserId: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return urlPatterns;
    }

    /**
     * Lấy tất cả URL patterns mà một role được phép truy cập
     * @param roleName Tên role
     * @return Set các URL patterns
     */
    public Set<String> getAllowedUrlPatternsByRoleName(String roleName) {
        Set<String> urlPatterns = new HashSet<>();
        String sql = "SELECT DISTINCT p.url_pattern " +
                     "FROM permissions p " +
                     "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
                     "INNER JOIN roles r ON rp.role_id = r.id " +
                     "WHERE r.role_name = ? AND p.url_pattern IS NOT NULL";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, roleName);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                String urlPattern = resultSet.getString("url_pattern");
                if (urlPattern != null && !urlPattern.isEmpty()) {
                    urlPatterns.add(urlPattern);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error in getAllowedUrlPatternsByRoleName: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return urlPatterns;
    }

    /**
     * Kiểm tra user có quyền truy cập URL không
     * @param userId ID của user
     * @param requestPath URL path cần kiểm tra (VD: /manager/machines)
     * @return true nếu có quyền, false nếu không
     */
    public boolean hasPermission(int userId, String requestPath) {
        Set<String> allowedPatterns = getAllowedUrlPatternsByUserId(userId);
        
        for (String pattern : allowedPatterns) {
            if (matchesUrl(requestPath, pattern)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Kiểm tra URL có khớp với pattern không
     * Hỗ trợ wildcard (*) ở cuối pattern
     * VD: /manager/machines khớp với /manager/machines
     *     /manager/machines/add khớp với /manager/machines (startsWith)
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

        // Support wildcard patterns: "/foo/*" or "/foo*"
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

        // StartsWith match (cho phép sub-paths)
        // VD: /manager/machines/add được phép nếu có quyền /manager/machines
        if (normalizedRequest.startsWith(basePattern + "/") || normalizedRequest.startsWith(basePattern + "?")) {
            return true;
        }

        return false;
    }

    /**
     * Lấy tất cả permissions
     * @return Danh sách tất cả Permission
     */
    public List<Permission> findAll() {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions ORDER BY permission_name";
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                Permission p = Permission.builder()
                        .id(resultSet.getInt("id"))
                        .permissionName(resultSet.getString("permission_name"))
                        .description(resultSet.getString("description"))
                        .urlPattern(resultSet.getString("url_pattern"))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .build();
                permissions.add(p);
            }
        } catch (SQLException ex) {
            System.out.println("Error in findAll: " + ex.getMessage());
        } finally {
            closeResources();
        }
        
        return permissions;
    }
}
