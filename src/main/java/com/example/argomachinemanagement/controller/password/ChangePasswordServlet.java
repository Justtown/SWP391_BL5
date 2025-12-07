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
import java.sql.ResultSet;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/change-password"})
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Integer userId = (Integer) session.getAttribute("userId");
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu hiện tại.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu mới.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng xác nhận mật khẩu mới.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Check password match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Check if new password is same as old password
        if (newPassword.equals(oldPassword)) {
            request.setAttribute("error", "Mật khẩu mới không được trùng với mật khẩu cũ.");
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        DBContext db = new DBContext();
        Connection conn = null;
        PreparedStatement checkPs = null;
        PreparedStatement updatePs = null;
        ResultSet rs = null;

        try {
            conn = db.getConnection();
            
            // Verify old password - hash để so sánh
            String checkSql = "SELECT password FROM users WHERE id = ?";
            checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, userId);
            rs = checkPs.executeQuery();
            
            if (rs.next()) {
                String currentPasswordHash = rs.getString("password");
                // Hash old password để so sánh
                String oldPasswordHash = MD5PasswordEncoderUtils.encodeMD5(oldPassword.trim());
                
                if (!oldPasswordHash.equals(currentPasswordHash)) {
                    request.setAttribute("error", "Mật khẩu hiện tại không đúng.");
                    request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
                    return;
                }
            } else {
                request.setAttribute("error", "Không tìm thấy tài khoản.");
                request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
                return;
            }
            
            // Update password - hash password mới
            String updateSql = "UPDATE users SET password = ? WHERE id = ?";
            updatePs = conn.prepareStatement(updateSql);
            String newPasswordHash = MD5PasswordEncoderUtils.encodeMD5(newPassword.trim());
            updatePs.setString(1, newPasswordHash);
            updatePs.setInt(2, userId);
            
            int rowsUpdated = updatePs.executeUpdate();
            
            if (rowsUpdated > 0) {
                request.setAttribute("message", "Đổi mật khẩu thành công!");
            } else {
                request.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Log chi tiết lỗi để debug
            System.err.println("Error in ChangePasswordServlet: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage() + ". Vui lòng thử lại sau.");
        } finally {
            // Close resources manually
            try {
                if (rs != null) rs.close();
                if (checkPs != null) checkPs.close();
                if (updatePs != null) updatePs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
    }
}

