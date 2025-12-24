package com.example.argomachinemanagement.controller.home;

import com.example.argomachinemanagement.dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session != null && session.getAttribute("userId") != null) {
            // User đã đăng nhập - redirect về dashboard theo role
            Integer userId = (Integer) session.getAttribute("userId");
            String redirectUrl = userDAO.getDefaultUrlByUserId(userId);
            response.sendRedirect(request.getContextPath() + redirectUrl);
            return;
        }
        
        // User chưa đăng nhập - redirect về login
        response.sendRedirect(request.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Home Servlet";
    }
}
