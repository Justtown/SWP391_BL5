//package com.example.argomachinemanagement.controller.admindashboard;
//
//import com.example.argomachinemanagement.dal.RoleDAO;
//import com.example.argomachinemanagement.entity.Role;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//import java.io.IOException;
//import java.util.List;
//
//@WebServlet("/admin/role-management")
//public class AdminRoleController extends HttpServlet {
//
//    private RoleDAO roleDAO = new RoleDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        String action = req.getParameter("action");
//
//        if ("detail".equals(action)) {
//            int id = Integer.parseInt(req.getParameter("id"));
//            Role role = roleDAO.getRoleById(id);
//            req.setAttribute("role", role);
//
//            req.getRequestDispatcher(
//                    "/view/dashboard/admin/role-detail.jsp"
//            ).forward(req, resp);
//        } else {
//            List<Role> roles = roleDAO.getAllRoles();
//            req.setAttribute("roles", roles);
//
//            req.getRequestDispatcher(
//                    "/view/dashboard/admin/role-list.jsp"
//            ).forward(req, resp);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
//            throws ServletException, IOException {
//
//        int roleId = Integer.parseInt(req.getParameter("roleId"));
//        String description = req.getParameter("description");
//        boolean status = "1".equals(req.getParameter("status"));
//
//        Role r = new Role();
//        r.setRoleId(roleId);
//        r.setDescription(description);
//        r.setStatus(status);
//
//        roleDAO.updateRole(r);
//
//        resp.sendRedirect(req.getContextPath() + "/admin/role-management");
//    }
//}
