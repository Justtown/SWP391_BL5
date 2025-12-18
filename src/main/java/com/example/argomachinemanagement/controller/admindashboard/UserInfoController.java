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
 * Controller xử lý việc xem và cập nhật thông tin user (chỉ dành cho admin)
 * URL: /user-info
 */
@WebServlet(name = "UserInfoController", urlPatterns = { "/user-info" })
public class UserInfoController extends HttpServlet {
    
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("id");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=User ID is required");
            return;
        }
        
        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=User not found");
                return;
            }
            
            // Lấy danh sách roles để hiển thị trong dropdown
            List<String> roles = userDAO.getAllRoles();
            
            request.setAttribute("user", user);
            request.setAttribute("roles", roles);
            
            // Forward to user info page
            request.getRequestDispatcher("/view/dashboard/admin/user-info.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=Invalid user ID");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userIdStr = request.getParameter("userId");
        String action = request.getParameter("action");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=User ID is required");
            return;
        }
        
        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userDAO.findById(userId);
            
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=User not found");
                return;
            }
            
            if ("update".equals(action)) {
                // Lấy dữ liệu từ form
                String roleName = request.getParameter("role");
                String statusStr = request.getParameter("status");
                
                // Validate
                if (roleName == null || roleName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Please select a role!");
                    doGet(request, response);
                    return;
                }
                
                // Parse status
                int status = 1; // default active
                if (statusStr != null && statusStr.equals("0")) {
                    status = 0;
                }
                
                // Cập nhật user
                user.setStatus(status);
                user.setRoleName(roleName);
                
                boolean success = userDAO.update(user);
                
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/manage-account?success="
                            + java.net.URLEncoder.encode("User updated successfully", "UTF-8"));
                } else {
                    request.setAttribute("errorMessage", "Failed to update user. Please try again!");
                    doGet(request, response);
                }
            } else {
                doGet(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/manage-account?error=Invalid user ID");
        }
    }
}

