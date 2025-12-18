package com.example.argomachinemanagement.controller.authen;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.PermissionDAO;
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
import java.util.Set;


@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {
    
    private UserDAO userDAO;
    private PasswordResetRequestDAO passwordResetRequestDAO;
    private PermissionDAO permissionDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
        permissionDAO = new PermissionDAO();
    }
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            // Đã đăng nhập, redirect đến dashboard theo role
            Integer userId = (Integer) session.getAttribute("userId");
            String redirectUrl = userDAO.getDefaultUrlByUserId(userId);
            response.sendRedirect(request.getContextPath() + redirectUrl);
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
        
        // Lấy user trong db (không check status trong query)
        User user = userDAO.login(username.trim(), password);

        if (user != null) {
            // Kiểm tra status của user
            // status = 0: Deactivated, 1: Active, 2: Pending
            if (user.getStatus() == 2) {
                // Tài khoản đang chờ duyệt
                request.setAttribute("error", "Tài khoản của bạn đang chờ Admin phê duyệt. Vui lòng đợi!");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
                return;
            } else if (user.getStatus() == 0) {
                // Tài khoản đã bị vô hiệu hóa
                request.setAttribute("error", "Tài khoản của bạn đã bị vô hiệu hóa. Vui lòng liên hệ Admin!");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/view/authen/login.jsp").forward(request, response);
                return;
            }

            // status = 1: Active - cho phép đăng nhập
            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("fullName", user.getFullName());
            session.setAttribute("roleName", user.getRoleName());

            // Load danh sách URL patterns được phép từ database và lưu vào session
            Set<String> allowedUrls = permissionDAO.getAllowedUrlPatternsByUserId(user.getId());
            session.setAttribute("allowedUrls", allowedUrls);

            // Kiểm tra xem user có login bằng password mới từ admin chưa (chưa đổi mật khẩu)
            PasswordResetRequest pendingChange = passwordResetRequestDAO.findUnchangedApprovedRequest(user.getId());
            if (pendingChange != null) {
                // Bắt user phải đổi mật khẩu (đã login bằng mật khẩu reset)
                session.setAttribute("mustChangePassword", true);
                response.sendRedirect(request.getContextPath() + "/change-password");
                return;
            }

            if ("on".equals(rememberMe)) {
                session.setMaxInactiveInterval(7 * 24 * 60 * 60); // 7 days
            } else {
                session.setMaxInactiveInterval(30 * 60); // 30 minutes
            }

            // Redirect theo role's default URL
            String redirectUrl = userDAO.getDefaultUrlByUserId(user.getId());
            response.sendRedirect(request.getContextPath() + redirectUrl);
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

