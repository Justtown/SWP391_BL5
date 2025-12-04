package com.mycompany.argomachinemanagement.src.controller.dashboard.admin;

import com.mycompany.argomachinemanagement.src.dal.RoleDAO;
import com.mycompany.argomachinemanagement.src.entity.Role;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/role")
public class RoleController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";   // mặc định
        }

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
        if (key == null) key = "";

        RoleDAO dao = new RoleDAO();
        List<Role> list = dao.findAll(key);

        request.setAttribute("list", list);
        request.getRequestDispatcher("role-list.jsp").forward(request, response);
    }

    private void showDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int id = Integer.parseInt(request.getParameter("id"));

        RoleDAO dao = new RoleDAO();
        Role role = dao.findById(id);

        request.setAttribute("role", role);
        request.getRequestDispatcher("role-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            updateRole(request, response);
        }
    }

    private void updateRole(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        int id = Integer.parseInt(request.getParameter("role_id"));
        String name = request.getParameter("role_name");
        String desc = request.getParameter("description");
        boolean status = Boolean.parseBoolean(request.getParameter("status"));

        Role r = new Role();
        r.setRoleId(id);
        r.setRoleName(name);
        r.setDescription(desc);
        r.setStatus(status);

        new RoleDAO().update(r);

        response.sendRedirect("role?action=list");
    }
}