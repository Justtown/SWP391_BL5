package com.example.argomachinemanagement.controller.password;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.PasswordResetRequest;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name = "PasswordController", urlPatterns = {"/forgot-password", "/reset-password"})
public class PasswordController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(PasswordController.class.getName());
    private UserDAO userDAO;
    private PasswordResetRequestDAO passwordResetRequestDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userDAO = new UserDAO();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/forgot-password".equals(path)) {
            // Hiển thị form forgot password
            request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
        } else if ("/reset-password".equals(path)) {
            // Hiển thị form reset password (không dùng nữa vì admin tự động tạo password mới)
            request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        
        if ("/forgot-password".equals(path)) {
            handleForgotPassword(request, response);
        } else if ("/reset-password".equals(path)) {
            // Không dùng nữa, redirect về forgot-password
            response.sendRedirect(request.getContextPath() + "/forgot-password");
        }
    }
    

    private void handleForgotPassword(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email của bạn.");
            request.setAttribute("mode", "forgot");
            request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
            return;
        }
        
        try {
            // Kiểm tra email có tồn tại trong hệ thống không
            User user = userDAO.findByEmail(email.trim());
            
            if (user == null) {
                request.setAttribute("error", "Email không tồn tại trong hệ thống.");
                request.setAttribute("mode", "forgot");
                request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
                return;
            }
            
            // Kiểm tra tài khoản có active không
            if (user.getStatus() == null || user.getStatus() != 1) {
                request.setAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ admin.");
                request.setAttribute("mode", "forgot");
                request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
                return;
            }
            
            // Tạo password reset request với status = "pending"
            PasswordResetRequest resetRequest = PasswordResetRequest.builder()
                    .userId(user.getId())
                    .email(user.getEmail())
                    .status("pending")
                    .passwordChanged(false)
                    .build();
            
            int requestId = passwordResetRequestDAO.createRequest(resetRequest);
            
            if (requestId > 0) {
                request.setAttribute("message", 
                    "Yêu cầu đặt lại mật khẩu đã được gửi thành công. " +
                    "Vui lòng chờ admin phê duyệt. Bạn sẽ nhận được email khi yêu cầu được duyệt.");
                request.setAttribute("mode", "success");
                logger.info("Password reset request created: ID=" + requestId + ", Email=" + email);
            } else {
                request.setAttribute("error", "Có lỗi xảy ra khi tạo yêu cầu. Vui lòng thử lại sau.");
                request.setAttribute("mode", "forgot");
                logger.warning("Failed to create password reset request for email: " + email);
            }
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error in handleForgotPassword", ex);
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau.");
            request.setAttribute("mode", "forgot");
        }
        
        request.getRequestDispatcher("/view/password/forgot_reset_password.jsp").forward(request, response);
    }
}

