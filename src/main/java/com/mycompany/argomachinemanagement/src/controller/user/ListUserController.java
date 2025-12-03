package com.mycompany.argomachinemanagement.src.controller.user;

import com.mycompany.argomachinemanagement.src.dal.AccountDAO;
import com.mycompany.argomachinemanagement.src.dto.UserDTO;
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
        String keyword = request.getParameter("keyword");
        List<UserDTO> users;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Search users by keyword
            users = accountDAO.searchUsers(keyword.trim());
            request.setAttribute("keyword", keyword);
        } else {
            // Get all users
            users = accountDAO.findAllUsersWithRole();
        }

        request.setAttribute("users", users);
        request.getRequestDispatcher("/view/user/list-user.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle POST request (search form submission)
        doGet(request, response);
    }
}

