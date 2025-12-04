package com.mycompany.argomachinemanagement.src.controller.user;

import com.mycompany.argomachinemanagement.src.dal.AccountDAO;
import com.mycompany.argomachinemanagement.src.entity.Account;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for listing users
 */
@WebServlet(name = "ListUserController", urlPatterns = {"/list-user"})
public class ListUserController extends HttpServlet {

    private AccountDAO accountDAO;

    @Override
    public void init() throws ServletException {
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get filter parameters
        String keyword = request.getParameter("keyword");
        String roleFilter = request.getParameter("role");
        String statusFilter = request.getParameter("status");
        
        // Convert status filter to Integer (null if "all" or empty)
        Integer status = null;
        if (statusFilter != null && !statusFilter.trim().isEmpty() && !statusFilter.equals("all")) {
            try {
                status = Integer.parseInt(statusFilter);
            } catch (NumberFormatException e) {
                status = null;
            }
        }
        
        // Get accounts with filters
        List<Account> accounts = accountDAO.findUsersWithFilters(
            keyword != null && !keyword.trim().isEmpty() ? keyword.trim() : null,
            roleFilter,
            status
        );
        
        // Get all roles for dropdown
        List<String> roles = accountDAO.getAllRoleNames();
        
        // Set attributes for JSP
        request.setAttribute("users", accounts);
        request.setAttribute("roles", roles);
        request.setAttribute("keyword", keyword != null ? keyword : "");
        request.setAttribute("selectedRole", roleFilter != null ? roleFilter : "all");
        request.setAttribute("selectedStatus", statusFilter != null ? statusFilter : "all");
        
        request.getRequestDispatcher("/view/user/list-user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle POST request (search form submission)
        doGet(request, response);
    }
}

