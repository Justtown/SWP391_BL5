package com.example.argomachinemanagement.controller.password;

import com.example.argomachinemanagement.dal.DBContext;
import com.example.argomachinemanagement.utils.MD5PasswordEncoderUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user has valid reset session
        if (session == null || session.getAttribute("resetUserId") == null) {
            response.sendRedirect(request.getContextPath() + "/forgot-password");
            return;
        }
        
        request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Validate session
        if (session == null || session.getAttribute("resetUserId") == null) {
            request.setAttribute("error", "Phiên làm việc đã hết hạn. Vui lòng thử lại.");
            request.getRequestDispatcher("/view/password/forgot_password.jsp").forward(request, response);
            return;
        }

        Integer userId = (Integer) session.getAttribute("resetUserId");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input
        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu mới.");
            request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng xác nhận mật khẩu.");
            request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
            return;
        }

        // Check password match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự.");
            request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
            return;
        }

        DBContext db = new DBContext();
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = db.getConnection();
            String sql = "UPDATE users SET password = ? WHERE id = ?";
            ps = conn.prepareStatement(sql);
            // Hash password với MD5 trước khi lưu
            String hashedPassword = MD5PasswordEncoderUtils.encodeMD5(newPassword.trim());
            ps.setString(1, hashedPassword);
            ps.setInt(2, userId);
            
            int rowsUpdated = ps.executeUpdate();
            
            if (rowsUpdated > 0) {
                session.removeAttribute("resetUserId");
                session.removeAttribute("resetEmail");
                
                request.setAttribute("message", "Đổi mật khẩu thành công! Vui lòng đăng nhập với mật khẩu mới.");
                request.getRequestDispatcher("/view/password/forgot_password.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Log chi tiết lỗi để debug
            System.err.println("Error in ResetPasswordServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage() + ". Vui lòng thử lại sau.");
        } finally {
            // Close resources manually
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/view/password/reset_password.jsp").forward(request, response);
    }
}

