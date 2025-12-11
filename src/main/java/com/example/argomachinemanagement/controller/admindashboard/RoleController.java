package com.example.argomachinemanagement.controller.admindashboard;

import com.example.argomachinemanagement.dal.RoleDAO;
import com.example.argomachinemanagement.entity.Role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/admin/role")
public class RoleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "detail":
                showDetail(request, response);
                break;
            case "list":
            default:
                showList(request, response);
                break;
        }
    }

    private void showList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String key = request.getParameter("key");
        if (key == null || key.equals("null")) key = "";

        int page = 1;
        try { page = Integer.parseInt(request.getParameter("page")); } catch (Exception ignored) {}
        int pageSize = 5;

        RoleDAO roleDao = new RoleDAO();
        int totalItems = roleDao.count(key);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (totalPages == 0) totalPages = 1;
        if (page < 1) page = 1;
        if (page > totalPages) page = totalPages;

        int offset = (page - 1) * pageSize;
        List<Role> roleList = roleDao.findByPage(key, offset, pageSize);

        System.out.println("RoleController.showList -> roleList.size = " + (roleList == null ? 0 : roleList.size()));

        request.setAttribute("roleList", roleList);
        request.setAttribute("key", key);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.getRequestDispatcher("/view/admin/role-list.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = 0;
        try { id = Integer.parseInt(request.getParameter("id")); } catch (Exception ignored) {}
        RoleDAO dao = new RoleDAO();
        Role role = dao.findById(id);
        System.out.println("RoleController.showDetail -> id=" + id + " role=" + (role==null?"null":"ok"));
        if (role == null) {
            response.sendRedirect(request.getContextPath() + "/admin/role?action=list");
            return;
        }
        request.setAttribute("role", role);
        request.getRequestDispatcher("/view/admin/role-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");
        if ("update".equals(action)) {
            updateRole(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/role?action=list");
        }
    }

    private void updateRole(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = 0;
        try { id = Integer.parseInt(request.getParameter("role_id")); } catch (Exception ignored) {}
        String name = request.getParameter("role_name");
        String desc = request.getParameter("description");
        boolean status = "1".equals(request.getParameter("status"));

        Role r = new Role();
        r.setRoleId(id);
        r.setRoleName(name);
        r.setDescription(desc);
        r.setStatus(status);

        new RoleDAO().update(r);
        response.sendRedirect(request.getContextPath() + "/admin/role?action=list");
    }
}
