package com.mycompany.argomachinemanagement.src.controller.authen;

import com.mycompany.argomachinemanagement.src.dal.UserDAO;
import com.mycompany.argomachinemanagement.src.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;


@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
                request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
    }

 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("remember");
        
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
            return;
        }
        
    
        User user = userDAO.login(username.trim(), password);
        
        if (user != null) {
   
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            
            
            if ("on".equals(rememberMe)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            } else {
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
            }
            
            
            String returnUrl = request.getParameter("returnUrl");
            if (returnUrl != null && !returnUrl.isEmpty()) {
                response.sendRedirect(returnUrl);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
        } else {
           
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.setAttribute("username", username); 
            request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login Servlet";
    }
}

