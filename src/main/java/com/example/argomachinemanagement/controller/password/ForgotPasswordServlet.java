package com.example.argomachinemanagement.controller.password;

import com.example.argomachinemanagement.dal.DBContext;
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

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/view/password/forgot_password.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");

        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập email.");
            request.getRequestDispatcher("/view/password/forgot_password.jsp").forward(request, response);
            return;
        }

        DBContext db = new DBContext();

        try {
            Connection conn = db.getConnection();
            String sql = "SELECT id FROM users WHERE email = ? AND status = 1";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("id");
                HttpSession session = request.getSession();
                session.setAttribute("resetUserId", userId);
                session.setAttribute("resetEmail", email.trim());
                rs.close();
                ps.close();
                db.closeResources();
                response.sendRedirect(request.getContextPath() + "/reset-password");
                return;
            } else {
                request.setAttribute("error", "Email không tồn tại hoặc tài khoản đã bị khóa.");
            }

            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra, vui lòng thử lại sau.");
        } finally {
            db.closeResources();
        }

        request.getRequestDispatcher("/view/password/forgot_password.jsp").forward(request, response);
    }
}
