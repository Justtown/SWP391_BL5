package com.example.argomachinemanagement.controller.authen;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.PasswordResetRequest;
import com.example.argomachinemanagement.entity.User;
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
    private PasswordResetRequestDAO passwordResetRequestDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
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
        
        // Lấy user trong db
        User user = userDAO.login(username.trim(), password);
        
        if (user != null) {
            // Kiểm tra xem có request approved nhưng đã hết hạn (quá 5 phút) không
            PasswordResetRequest expiredRequest = passwordResetRequestDAO.findExpiredApprovedRequest(user.getId());
            if (expiredRequest != null) {
                // Password đã hết hạn, yêu cầu user request lại
                request.setAttribute("error", "Mật khẩu được reset đã hết hạn (5 phút). Vui lòng yêu cầu reset mật khẩu lại!");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
                return;
            }
            
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            
            // Kiểm tra xem user có login bằng password mới từ admin chưa (chưa đổi mật khẩu, còn trong 5 phút)
            PasswordResetRequest pendingChange = passwordResetRequestDAO.findUnchangedApprovedRequest(user.getId());
            if (pendingChange != null) {
                // Bắt user phải đổi mật khẩu
                session.setAttribute("mustChangePassword", true);
                response.sendRedirect(request.getContextPath() + "/change-password");
                return;
            }
            
            if ("on".equals(rememberMe)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            } else {
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
            }

            response.sendRedirect(request.getContextPath() + "/home");
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

