package com.example.argomachinemanagement.controller.admindashboard;

import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller xử lý danh sách users chờ duyệt (status=2)
 * URL: /admin/pending-users
 */
@WebServlet(name = "PendingUsersController", urlPatterns = {"/admin/pending-users"})
public class PendingUsersController extends HttpServlet {

    private static final int PENDING_STATUS = 2;
    private static final int ACTIVE_STATUS = 1;
    private static final int DEACTIVATED_STATUS = 0;

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy danh sách users chờ duyệt (status = 2)
        List<User> pendingUsers = userDAO.findByStatus(PENDING_STATUS);

        request.setAttribute("pendingUsers", pendingUsers);
        request.setAttribute("totalPending", pendingUsers.size());

        // Kiểm tra success/error messages từ redirect
        String successMsg = request.getParameter("success");
        String errorMsg = request.getParameter("error");
        if (successMsg != null) {
            request.setAttribute("successMessage", successMsg);
        }
        if (errorMsg != null) {
            request.setAttribute("errorMessage", errorMsg);
        }

        request.getRequestDispatcher("/view/dashboard/admin/pending-users.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=User ID is required");
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=User not found");
                return;
            }

            if (user.getStatus() != PENDING_STATUS) {
                response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=User is not in pending status");
                return;
            }

            boolean success = false;
            String message = "";

            if ("approve".equals(action)) {
                // Phê duyệt user - set status = 1 (Active)
                success = userDAO.updateStatus(userId, ACTIVE_STATUS);
                message = success ? "Đã phê duyệt user: " + user.getFullName() : "Lỗi khi phê duyệt user";
            } else if ("reject".equals(action)) {
                // Từ chối user - set status = 0 (Deactivated)
                success = userDAO.updateStatus(userId, DEACTIVATED_STATUS);
                message = success ? "Đã từ chối user: " + user.getFullName() : "Lỗi khi từ chối user";
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=Invalid action");
                return;
            }

            if (success) {
                response.sendRedirect(request.getContextPath() + "/admin/pending-users?success=" + java.net.URLEncoder.encode(message, "UTF-8"));
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=" + java.net.URLEncoder.encode(message, "UTF-8"));
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/pending-users?error=Invalid user ID");
        }
    }

    @Override
    public String getServletInfo() {
        return "Pending Users Controller - Admin approval workflow";
    }
}
