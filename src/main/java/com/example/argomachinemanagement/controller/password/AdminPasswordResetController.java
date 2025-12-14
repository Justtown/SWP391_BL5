package com.example.argomachinemanagement.controller.password;

import com.example.argomachinemanagement.dal.PasswordResetRequestDAO;
import com.example.argomachinemanagement.dal.UserDAO;
import com.example.argomachinemanagement.entity.PasswordResetRequest;
import com.example.argomachinemanagement.entity.User;
import com.example.argomachinemanagement.utils.EmailService;
import com.example.argomachinemanagement.utils.MD5PasswordEncoderUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


@WebServlet(name = "AdminPasswordResetController", urlPatterns = {"/admin/password-reset-requests"})
public class AdminPasswordResetController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(AdminPasswordResetController.class.getName());
    private static final int PAGE_SIZE = 10;
    
    private PasswordResetRequestDAO passwordResetRequestDAO;
    private UserDAO userDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        passwordResetRequestDAO = new PasswordResetRequestDAO();
        userDAO = new UserDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin authentication
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin only.");
            return;
        }
        
        // Lấy các tham số search, filter, pagination
        String keyword = request.getParameter("keyword");
        String statusFilter = request.getParameter("status");
        String pageStr = request.getParameter("page");
        
        if (keyword == null) keyword = "";
        if (statusFilter == null || statusFilter.isEmpty()) statusFilter = "all";
        
        int currentPage = 1;
        try {
            if (pageStr != null && !pageStr.isEmpty()) {
                currentPage = Integer.parseInt(pageStr);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
        
        // Đếm tổng số records
        int totalRecords = passwordResetRequestDAO.countRequests(keyword, statusFilter);
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);
        
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        
        // Tính offset
        int offset = (currentPage - 1) * PAGE_SIZE;
        
        // Lấy danh sách requests với search, filter và pagination
        List<PasswordResetRequest> requests = passwordResetRequestDAO.getAllRequests(keyword, statusFilter, offset, PAGE_SIZE);
        
        HttpSession session = request.getSession();
        String message = (String) session.getAttribute("message");
        String error = (String) session.getAttribute("error");
        
        if (message != null) {
            request.setAttribute("message", message);
            session.removeAttribute("message");
        }
        if (error != null) {
            request.setAttribute("error", error);
            session.removeAttribute("error");
        }
        
        request.setAttribute("requests", requests);
        request.setAttribute("keyword", keyword);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("pageSize", PAGE_SIZE);
        
        request.getRequestDispatcher("/view/dashboard/admin/password_reset_requests.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check admin authentication
        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied. Admin only.");
            return;
        }
        
        String action = request.getParameter("action");
        String requestIdStr = request.getParameter("requestId");
        
        if (action == null || requestIdStr == null) {
            HttpSession session = request.getSession();
            session.setAttribute("error", "Thiếu thông tin yêu cầu.");
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
            return;
        }
        
        try {
            Integer requestId = Integer.parseInt(requestIdStr);
            PasswordResetRequest resetRequest = passwordResetRequestDAO.findById(requestId);
            
            if (resetRequest == null) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Không tìm thấy yêu cầu đặt lại mật khẩu.");
                response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
                return;
            }
            
            if ("approve".equals(action)) {
                handleApproveRequest(resetRequest, request, response);
            } else if ("reject".equals(action)) {
                handleRejectRequest(resetRequest, request, response);
            } else {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Hành động không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
            }
            
        } catch (NumberFormatException ex) {
            logger.log(Level.WARNING, "Invalid requestId format: " + requestIdStr, ex);
            HttpSession session = request.getSession();
            session.setAttribute("error", "ID yêu cầu không hợp lệ.");
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error processing password reset request", ex);
            HttpSession session = request.getSession();
            session.setAttribute("error", "Có lỗi xảy ra khi xử lý yêu cầu.");
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
        }
    }
    

     // Xử lý approve request: Generate password mới, hash và lưu vào DB, gửi email plain text

    private void handleApproveRequest(PasswordResetRequest resetRequest, 
                                     HttpServletRequest request, 
                                     HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra xem request đã được xử lý chưa (chỉ xử lý nếu status là "pending")
        if (!"pending".equalsIgnoreCase(resetRequest.getStatus())) {
            HttpSession session = request.getSession();
            session.setAttribute("message", "Yêu cầu này đã được xử lý trước đó.");
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
            return;
        }
        
        try {
            // Generate password mới (8 ký tự, chữ + số)
            String newPassword = generateRandomPassword(8);
            // Hash password để lưu vào database
            String hashedPassword = MD5PasswordEncoderUtils.encodeMD5(newPassword);
            
            // Cập nhật password đã hash vào database
            boolean updateSuccess = passwordResetRequestDAO.updateUserPassword(
                resetRequest.getUserId(), hashedPassword);
            
            if (!updateSuccess) {
                HttpSession session = request.getSession();
                session.setAttribute("error", "Không thể cập nhật mật khẩu. Vui lòng thử lại.");
                response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
                return;
            }
            
            // Cập nhật status và lưu password mới vào request để gửi email
            boolean statusUpdateSuccess = passwordResetRequestDAO.updateRequestStatus(
                resetRequest.getId(), "approved", newPassword);
            
            if (!statusUpdateSuccess) {
                logger.warning("Failed to update request status to approved for requestId: " + resetRequest.getId());
            }
            
            // Gửi email với mật khẩu mới
            boolean emailSent = EmailService.sendPasswordResetEmail(
                resetRequest.getEmail(), newPassword);
            
            HttpSession session = request.getSession();
            if (emailSent) {
                session.setAttribute("message", 
                    "Đã phê duyệt yêu cầu và gửi email mật khẩu mới đến: " + resetRequest.getEmail());
                logger.info("Password reset approved and email sent for requestId: " + resetRequest.getId() + 
                           ", UserId: " + resetRequest.getUserId());
            } else {
                session.setAttribute("message", 
                    "Đã phê duyệt yêu cầu và cập nhật mật khẩu, nhưng không thể gửi email. " +
                    "Vui lòng kiểm tra cấu hình email hoặc liên hệ user trực tiếp. " +
                    "Mật khẩu mới đã được lưu trong hệ thống.");
                logger.warning("Password reset approved but email failed for requestId: " + resetRequest.getId());
            }
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error approving password reset request", ex);
            HttpSession session = request.getSession();
            session.setAttribute("error", "Có lỗi xảy ra khi phê duyệt yêu cầu.");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
    }
    

     // Xử lý reject request

    private void handleRejectRequest(PasswordResetRequest resetRequest,
                                    HttpServletRequest request,
                                    HttpServletResponse response)
            throws ServletException, IOException {
        
        // Kiểm tra xem request đã được xử lý chưa
        if (!"pending".equalsIgnoreCase(resetRequest.getStatus())) {
            HttpSession session = request.getSession();
            session.setAttribute("message", "Yêu cầu này đã được xử lý trước đó.");
            response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
            return;
        }
        
        try {
            boolean success = passwordResetRequestDAO.updateRequestStatus(
                resetRequest.getId(), "rejected", null);
            
            HttpSession session = request.getSession();
            if (success) {
                session.setAttribute("message", "Đã từ chối yêu cầu đặt lại mật khẩu.");
                logger.info("Password reset request rejected: ID=" + resetRequest.getId());
            } else {
                session.setAttribute("error", "Không thể từ chối yêu cầu. Vui lòng thử lại.");
            }
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error rejecting password reset request", ex);
            HttpSession session = request.getSession();
            session.setAttribute("error", "Có lỗi xảy ra khi từ chối yêu cầu.");
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/password-reset-requests");
    }
    

     // Generate random password

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    

     // Kiểm tra user có phải admin không

    private boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return false;
        }
        
        // Lấy user từ database để kiểm tra role
        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }
        
        // Check if user has admin role
        return "admin".equalsIgnoreCase(user.getRoleName());
    }
}

