package com.example.argomachinemanagement.controller.password;

import com.example.argomachinemanagement.dal.DBContext;
import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
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

@WebServlet(name = "ChangePasswordController", urlPatterns = {"/change-password"})
public class ChangePasswordController extends HttpServlet {

    private PasswordResetRequestDAO passwordResetRequestDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        
        // Check if user needs to change password (login với password mới từ admin)
        boolean mustChangePassword = session.getAttribute("mustChangePassword") != null 
                && (Boolean) session.getAttribute("mustChangePassword");
        request.setAttribute("mustChangePassword", mustChangePassword);
        
        if (mustChangePassword) {
            request.setAttribute("warning", "Bạn đã đăng nhập bằng mật khẩu mới. Vui lòng đổi mật khẩu ngay để bảo mật tài khoản.");
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
        boolean mustChangePassword = session.getAttribute("mustChangePassword") != null 
                && (Boolean) session.getAttribute("mustChangePassword");
        
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Validate input
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu hiện tại.");
            request.setAttribute("mustChangePassword", mustChangePassword);
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập mật khẩu mới.");
            request.setAttribute("mustChangePassword", mustChangePassword);
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng xác nhận mật khẩu mới.");
            request.setAttribute("mustChangePassword", mustChangePassword);
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Check password match
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp.");
            request.setAttribute("mustChangePassword", mustChangePassword);
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Validate password strength
        if (newPassword.length() < 6) {
            request.setAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự.");
            request.setAttribute("mustChangePassword", mustChangePassword);
            request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
            return;
        }

        // Check if new password is same as old password
        if (newPassword.equals(oldPassword)) {
            request.setAttribute("error", "Mật khẩu mới không được trùng với mật khẩu cũ.");
            request.setAttribute("mustChangePassword", mustChangePassword);
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
                    request.setAttribute("mustChangePassword", mustChangePassword);
                    request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
                    return;
                }
            } else {
                request.setAttribute("error", "Không tìm thấy tài khoản.");
                request.setAttribute("mustChangePassword", mustChangePassword);
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
                // Nếu đổi mật khẩu thành công và user đang bị bắt đổi mật khẩu
                if (mustChangePassword) {
                    // Đánh dấu đã đổi mật khẩu trong password_reset_requests
                    passwordResetRequestDAO.markPasswordAsChanged(userId);
                    // Xóa flag mustChangePassword khỏi session
                    session.removeAttribute("mustChangePassword");
                }
                
                request.setAttribute("message", "Đổi mật khẩu thành công!");
                
                // Nếu bắt buộc đổi mật khẩu, redirect về home sau 2 giây
                if (mustChangePassword) {
                    response.sendRedirect(request.getContextPath() + "/home?passwordChanged=true");
                    return;
                }
            } else {
                request.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in ChangePasswordController: " + e.getMessage());
            request.setAttribute("error", "Có lỗi xảy ra: " + e.getMessage() + ". Vui lòng thử lại sau.");
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkPs != null) checkPs.close();
                if (updatePs != null) updatePs.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.setAttribute("mustChangePassword", mustChangePassword);
        request.getRequestDispatcher("/view/password/change_password.jsp").forward(request, response);
    }
}

