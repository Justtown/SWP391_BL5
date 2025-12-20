//package com.example.argomachinemanagement.controller.admin;
//
//import com.example.argomachinemanagement.dal.PermissionDAO;
//import com.example.argomachinemanagement.dal.RoleDAO;
//import com.example.argomachinemanagement.entity.Permission;
//import com.example.argomachinemanagement.entity.Role;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//import java.io.IOException;
//import java.util.List;
//
//@WebServlet(name = "AdminPermissionServlet", urlPatterns = {"/admin/permissions"})
//public class AdminPermissionServlet extends HttpServlet {
//    private PermissionDAO permissionDAO = new PermissionDAO();
//    private RoleDAO roleDAO = new RoleDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("userId") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        String userRole = (String) session.getAttribute("roleName");
//        if (!"admin".equals(userRole)) {
//            response.sendError(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }
//
//        String action = request.getParameter("action");
//        if (action == null) action = "matrix";
//
//        switch (action) {
//            case "matrix": handleMatrix(request, response); break;
//            case "add": handleShowAdd(request, response); break;
//            case "edit": handleShowEdit(request, response); break;
//            case "delete": handleDelete(request, response); break;
//            case "role-permissions": handleRolePermissions(request, response); break;
//            default: response.sendRedirect(request.getContextPath() + "/admin/permissions?action=matrix");
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("userId") == null) {
//            response.sendRedirect(request.getContextPath() + "/login");
//            return;
//        }
//
//        String action = request.getParameter("action");
//        switch (action) {
//            case "create": handleCreate(request, response); break;
//            case "update": handleUpdate(request, response); break;
//            case "assign": handleAssign(request, response); break;
//            case "remove": handleRemove(request, response); break;
//            case "save-matrix": handleSaveMatrix(request, response); break;
//            default: response.sendRedirect(request.getContextPath() + "/admin/permissions");
//        }
//    }
//
//    private void handleShowAdd(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.getRequestDispatcher("/view/admin/permission-form.jsp").forward(request, response);
//    }
//
//    private void handleShowEdit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            Permission permission = permissionDAO.findById(id);
//            if (permission != null) {
//                request.setAttribute("permission", permission);
//                request.getRequestDispatcher("/view/admin/permission-form.jsp").forward(request, response);
//            } else {
//                response.sendRedirect(request.getContextPath() + "/admin/permissions?error=notfound");
//            }
//        } catch (Exception e) {
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?error=invalid");
//        }
//    }
//
//    private void handleCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String name = request.getParameter("permissionName");
//        String desc = request.getParameter("description");
//        String url = request.getParameter("urlPattern");
//        boolean success = permissionDAO.create(name, desc, url);
//        response.sendRedirect(request.getContextPath() + "/admin/permissions?success=" + (success ? "created" : "failed"));
//    }
//
//    private void handleUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            String name = request.getParameter("permissionName");
//            String desc = request.getParameter("description");
//            String url = request.getParameter("urlPattern");
//            boolean success = permissionDAO.update(id, name, desc, url);
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?success=" + (success ? "updated" : "failed"));
//        } catch (Exception e) {
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?error=invalid");
//        }
//    }
//
//    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int id = Integer.parseInt(request.getParameter("id"));
//            boolean success = permissionDAO.delete(id);
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?success=" + (success ? "deleted" : "failed"));
//        } catch (Exception e) {
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?error=invalid");
//        }
//    }
//
//    private void handleRolePermissions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Role> roles = roleDAO.findAll();
//        List<Permission> allPermissions = permissionDAO.findAll();
//        request.setAttribute("roles", roles);
//        request.setAttribute("allPermissions", allPermissions);
//
//        String roleIdParam = request.getParameter("roleId");
//        if (roleIdParam != null) {
//            try {
//                int roleId = Integer.parseInt(roleIdParam);
//                List<Permission> rolePermissions = permissionDAO.getPermissionsByRoleId(roleId);
//                request.setAttribute("selectedRoleId", roleId);
//                request.setAttribute("rolePermissions", rolePermissions);
//            } catch (Exception e) {}
//        }
//        request.getRequestDispatcher("/view/admin/role-permissions.jsp").forward(request, response);
//    }
//
//    private void handleAssign(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int roleId = Integer.parseInt(request.getParameter("roleId"));
//            int permissionId = Integer.parseInt(request.getParameter("permissionId"));
//            boolean success = permissionDAO.assignPermissionToRole(roleId, permissionId);
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=role-permissions&roleId=" + roleId + "&success=" + (success ? "assigned" : "failed"));
//        } catch (Exception e) {
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=role-permissions&error=invalid");
//        }
//    }
//
//    private void handleRemove(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            int roleId = Integer.parseInt(request.getParameter("roleId"));
//            int permissionId = Integer.parseInt(request.getParameter("permissionId"));
//            boolean success = permissionDAO.removePermissionFromRole(roleId, permissionId);
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=role-permissions&roleId=" + roleId + "&success=" + (success ? "removed" : "failed"));
//        } catch (Exception e) {
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=role-permissions&error=invalid");
//        }
//    }
//
//    private void handleMatrix(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        List<Role> roles = roleDAO.findAll();
//        List<Permission> allPermissions = permissionDAO.findAll();
//
//        // Load permissions for each role
//        java.util.Map<Integer, List<Permission>> rolePermissionsMap = new java.util.HashMap<>();
//        for (Role role : roles) {
//            List<Permission> rolePerms = permissionDAO.getPermissionsByRoleId(role.getRoleId());
//            rolePermissionsMap.put(role.getRoleId(), rolePerms);
//        }
//
//        request.setAttribute("roles", roles);
//        request.setAttribute("allPermissions", allPermissions);
//        request.setAttribute("rolePermissionsMap", rolePermissionsMap);
//        request.getRequestDispatcher("/view/admin/permission-matrix.jsp").forward(request, response);
//    }
//
//    private void handleSaveMatrix(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        try {
//            List<Role> roles = roleDAO.findAll();
//            List<Permission> allPermissions = permissionDAO.findAll();
//
//            // Xóa tất cả role_permission hiện tại
//            for (Role role : roles) {
//                List<Permission> currentPerms = permissionDAO.getPermissionsByRoleId(role.getRoleId());
//                for (Permission perm : currentPerms) {
//                    permissionDAO.removePermissionFromRole(role.getRoleId(), perm.getId());
//                }
//            }
//
//            // Thêm lại các permission được chọn
//            for (Role role : roles) {
//                for (Permission perm : allPermissions) {
//                    String paramName = "permission_" + role.getRoleId() + "_" + perm.getId();
//                    String value = request.getParameter(paramName);
//                    if ("1".equals(value)) {
//                        permissionDAO.assignPermissionToRole(role.getRoleId(), perm.getId());
//                    }
//                }
//            }
//
//            // Clear session allowedUrls để reload lại permissions
//            request.getSession().removeAttribute("allowedUrls");
//
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=matrix&success=saved");
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.sendRedirect(request.getContextPath() + "/admin/permissions?action=matrix&error=failed");
//        }
//    }
//}
