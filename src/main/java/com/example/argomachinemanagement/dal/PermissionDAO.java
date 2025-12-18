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
        
        // Exact match
        if (requestPath.equals(pattern)) {
            return true;
        }
        
        // StartsWith match (cho phép sub-paths)
        // VD: /manager/machines/add được phép nếu có quyền /manager/machines
        if (requestPath.startsWith(pattern + "/") || requestPath.startsWith(pattern + "?")) {
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

    public Permission findById(int id) {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return Permission.builder()
                        .id(resultSet.getInt("id"))
                        .permissionName(resultSet.getString("permission_name"))
                        .description(resultSet.getString("description"))
                        .urlPattern(resultSet.getString("url_pattern"))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .build();
            }
        } catch (SQLException ex) {
            System.out.println("Error in findById: " + ex.getMessage());
        } finally {
            closeResources();
        }
        return null;
    }

    public boolean create(String permissionName, String description, String urlPattern) {
        String sql = "INSERT INTO permissions (permission_name, description, url_pattern) VALUES (?, ?, ?)";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, permissionName);
            statement.setString(2, description);
            statement.setString(3, urlPattern);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in create: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean update(int id, String permissionName, String description, String urlPattern) {
        String sql = "UPDATE permissions SET permission_name = ?, description = ?, url_pattern = ? WHERE id = ?";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, permissionName);
            statement.setString(2, description);
            statement.setString(3, urlPattern);
            statement.setInt(4, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in update: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean delete(int id) {
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            
            statement = connection.prepareStatement("DELETE FROM role_permission WHERE permission_id = ?");
            statement.setInt(1, id);
            statement.executeUpdate();
            statement.close();
            
            statement = connection.prepareStatement("DELETE FROM permissions WHERE id = ?");
            statement.setInt(1, id);
            int rows = statement.executeUpdate();
            
            connection.commit();
            return rows > 0;
        } catch (SQLException ex) {
            try { if (connection != null) connection.rollback(); } catch (SQLException e) {}
            System.out.println("Error in delete: " + ex.getMessage());
            return false;
        } finally {
            try { if (connection != null) connection.setAutoCommit(true); } catch (SQLException e) {}
            closeResources();
        }
    }

    public List<Permission> getPermissionsByRoleId(int roleId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p INNER JOIN role_permission rp ON p.id = rp.permission_id WHERE rp.role_id = ? ORDER BY p.permission_name";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, roleId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                permissions.add(Permission.builder()
                        .id(resultSet.getInt("id"))
                        .permissionName(resultSet.getString("permission_name"))
                        .description(resultSet.getString("description"))
                        .urlPattern(resultSet.getString("url_pattern"))
                        .createdAt(resultSet.getTimestamp("created_at"))
                        .build());
            }
        } catch (SQLException ex) {
            System.out.println("Error in getPermissionsByRoleId: " + ex.getMessage());
        } finally {
            closeResources();
        }
        return permissions;
    }

    public boolean assignPermissionToRole(int roleId, int permissionId) {
        String sql = "INSERT IGNORE INTO role_permission (role_id, permission_id) VALUES (?, ?)";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, roleId);
            statement.setInt(2, permissionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in assignPermissionToRole: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }

    public boolean removePermissionFromRole(int roleId, int permissionId) {
        String sql = "DELETE FROM role_permission WHERE role_id = ? AND permission_id = ?";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, roleId);
            statement.setInt(2, permissionId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error in removePermissionFromRole: " + ex.getMessage());
            return false;
        } finally {
            closeResources();
        }
    }
}
