package com.example.argomachinemanagement.controller.profile;

import com.example.argomachinemanagement.dal.ProfileDAO;
import com.example.argomachinemanagement.entity.Profile;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "ProfileController", urlPatterns = {"/profile", "/my-profile"})
public class ProfileController extends HttpServlet {
    
    private ProfileDAO profileDAO;
    
    @Override
    public void init() throws ServletException {
        profileDAO = new ProfileDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập - nếu chưa đăng nhập thì redirect về trang login
        Integer userId = (Integer) (session != null ? session.getAttribute("userId") : null);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?message=Vui lòng đăng nhập để xem profile!");
            return;
        }
        
        try {
            // Debug: Log userId
            System.out.println("ProfileController doGet - userId from session: " + userId);
            
            // Lấy thông tin profile từ bảng users
            Profile profile = profileDAO.getProfileByUserId(userId);
            
            // Debug: Log profile result
            if (profile != null) {
                System.out.println("Profile found - Name: " + profile.getName() + ", Email: " + profile.getEmail());
            } else {
                System.out.println("Profile is NULL for userId: " + userId);
            }
            
            // Luôn set profile vào request attribute (có thể null)
            request.setAttribute("profile", profile);
            
            // Nếu không tìm thấy user, hiển thị lỗi nhưng vẫn forward để hiển thị trang
            if (profile == null) {
                request.setAttribute("error", "Không tìm thấy thông tin người dùng! Vui lòng liên hệ quản trị viên.");
            } else {
                // Set default values nếu null
                if (profile.getPhone() == null) profile.setPhone("");
                if (profile.getAddress() == null) profile.setAddress("");
                if (profile.getAvatar() == null) profile.setAvatar("");
                if (profile.getRoleName() == null) profile.setRoleName("Customer");
            }
            
            // Kiểm tra chế độ edit (mặc định là view - chỉ edit khi có ?mode=edit)
            String mode = request.getParameter("mode");
            boolean isEditMode = mode != null && "edit".equals(mode.trim());
            // Đảm bảo mặc định là false (view mode)
            request.setAttribute("isEditMode", isEditMode);
            
            // Lấy thông báo từ session (nếu có) và xóa sau khi lấy
            if (session != null) {
                String successMessage = (String) session.getAttribute("successMessage");
                String errorMessage = (String) session.getAttribute("errorMessage");
                if (successMessage != null) {
                    request.setAttribute("success", successMessage);
                    session.removeAttribute("successMessage");
                }
                if (errorMessage != null) {
                    request.setAttribute("error", errorMessage);
                    session.removeAttribute("errorMessage");
                }
            }
            
            request.getRequestDispatcher("/view/profile/my-profile.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Đã xảy ra lỗi khi tải thông tin profile: " + e.getMessage());
            request.getRequestDispatcher("/view/profile/my-profile.jsp").forward(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // Kiểm tra đăng nhập - nếu chưa đăng nhập thì redirect về trang login
        Integer userId = (Integer) (session != null ? session.getAttribute("userId") : null);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login?message=Vui lòng đăng nhập để cập nhật profile!");
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("update".equals(action)) {
            // Lấy dữ liệu từ form
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String avatar = request.getParameter("avatar");
            String birthdateStr = request.getParameter("birthdate");
            
            // Validate
            if (name == null || name.trim().isEmpty()) {
                request.setAttribute("error", "Tên không được để trống!");
                doGet(request, response);
                return;
            }
            
            name = name.trim();
            if (name.length() < 2 || name.length() > 100) {
                request.setAttribute("error", "Tên phải có từ 2-100 ký tự!");
                doGet(request, response);
                return;
            }
            
            if (email == null || email.trim().isEmpty()) {
                request.setAttribute("error", "Email không được để trống!");
                doGet(request, response);
                return;
            }
            
            email = email.trim();
            if (email.length() > 255) {
                request.setAttribute("error", "Email không được vượt quá 255 ký tự!");
                doGet(request, response);
                return;
            }
            
            // Validate email format
            String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailRegex)) {
                request.setAttribute("error", "Email không đúng định dạng! Ví dụ: example@gmail.com");
                doGet(request, response);
                return;
            }
            
            // Validate Phone - Số điện thoại Việt Nam: 10 số (bắt đầu bằng 0) hoặc 11 số hoặc 12 số (+84)
            if (phone != null && !phone.trim().isEmpty()) {
                phone = phone.trim();
                // Loại bỏ các ký tự không phải số
                String phoneDigits = phone.replaceAll("[^0-9]", "");
                
                // Kiểm tra số điện thoại Việt Nam hợp lệ
                // - 10 số: bắt đầu bằng 0 (ví dụ: 0123456789)
                // - 11 số: có thể là số quốc tế hoặc số di động dài (bắt đầu bằng 0 hoặc 84)
                // - 12 số: số quốc tế +84 (ví dụ: +84945646654 -> 84945646654)
                if (phoneDigits.length() == 10) {
                    // Phải bắt đầu bằng 0
                    if (!phoneDigits.startsWith("0")) {
                        request.setAttribute("error", "Số điện thoại 10 số phải bắt đầu bằng 0!");
                        doGet(request, response);
                        return;
                    }
                    phone = phoneDigits;
                } else if (phoneDigits.length() == 11) {
                    // Có thể bắt đầu bằng 0 hoặc 84
                    if (!phoneDigits.startsWith("0") && !phoneDigits.startsWith("84")) {
                        request.setAttribute("error", "Số điện thoại 11 số phải bắt đầu bằng 0 hoặc 84!");
                        doGet(request, response);
                        return;
                    }
                    // Chuẩn hóa: nếu bắt đầu bằng 84 thì chuyển thành 0
                    if (phoneDigits.startsWith("84")) {
                        phone = "0" + phoneDigits.substring(2);
                    } else {
                        phone = phoneDigits;
                    }
                } else if (phoneDigits.length() == 12 && phoneDigits.startsWith("84")) {
                    // Số quốc tế +84 (12 số): chuyển thành 10 số bắt đầu bằng 0
                    phone = "0" + phoneDigits.substring(2);
                } else if (phoneDigits.length() < 10 || phoneDigits.length() > 12) {
                    request.setAttribute("error", "Số điện thoại không hợp lệ! Phải có từ 10-12 chữ số.");
                    doGet(request, response);
                    return;
                } else {
                    request.setAttribute("error", "Số điện thoại không hợp lệ!");
                    doGet(request, response);
                    return;
                }
            } else {
                phone = null; // Set null nếu null hoặc empty
            }
            
            // Validate Address
            if (address != null && address.length() > 500) {
                request.setAttribute("error", "Địa chỉ không được vượt quá 500 ký tự!");
                doGet(request, response);
                return;
            }
            
            // Validate Birthdate
            Date birthdate = null;
            if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
                try {
                    birthdate = Date.valueOf(birthdateStr);
                    Date today = new Date(System.currentTimeMillis());
                    
                    if (birthdate.after(today)) {
                        request.setAttribute("error", "Ngày sinh không được là tương lai!");
                        doGet(request, response);
                        return;
                    }
                    
                    // Kiểm tra ngày sinh không quá 120 năm trước
                    long diffInMillies = today.getTime() - birthdate.getTime();
                    long diffInYears = diffInMillies / (1000L * 60 * 60 * 24 * 365);
                    
                    if (diffInYears > 120) {
                        request.setAttribute("error", "Ngày sinh không hợp lệ!");
                        doGet(request, response);
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", "Ngày sinh không hợp lệ!");
                    doGet(request, response);
                    return;
                }
            }
            
            try {
                // Tạo Profile object để update
                Profile profile = new Profile();
                profile.setUserId(userId);
                profile.setName(name);
                profile.setEmail(email);
                // Phone đã được validate và chuẩn hóa ở trên, có thể là empty string hoặc null
                profile.setPhone(phone != null && !phone.isEmpty() ? phone : null);
                profile.setAddress(address != null && !address.trim().isEmpty() ? address.trim() : null);
                profile.setAvatar(avatar != null && !avatar.trim().isEmpty() ? avatar.trim() : null);
                profile.setBirthdate(birthdate);
                
                // Log để debug
                System.out.println("Updating profile for userId: " + userId);
                System.out.println("Profile data - Name: " + name + ", Email: " + email + ", Phone: " + phone);
                System.out.println("Profile - Address: " + profile.getAddress() + ", Avatar: " + profile.getAvatar());
                
                // Lưu profile (tạo mới hoặc cập nhật)
                boolean success = profileDAO.saveProfile(profile);
                
                if (success) {
                    if (session != null) {
                        session.setAttribute("successMessage", "Cập nhật thông tin thành công!");
                    }
                    // Sau khi cập nhật thành công, chuyển về chế độ view (mặc định)
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                } else {
                    // Log lỗi chi tiết
                    System.err.println("Failed to save profile for userId: " + userId);
                    if (session != null) {
                        session.setAttribute("errorMessage", "Cập nhật thông tin thất bại! Vui lòng kiểm tra lại thông tin và thử lại.");
                    }
                    response.sendRedirect(request.getContextPath() + "/profile?mode=edit");
                    return;
                }
            } catch (Exception e) {
                // Log exception chi tiết
                System.err.println("Exception when updating profile: " + e.getMessage());
                e.printStackTrace();
                if (session != null) {
                    session.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật profile: " + e.getMessage());
                }
                response.sendRedirect(request.getContextPath() + "/profile?mode=edit");
                return;
            }
        } else {
            if (session != null) {
                session.setAttribute("errorMessage", "Hành động không hợp lệ!");
            }
            response.sendRedirect(request.getContextPath() + "/profile");
            return;
        }
    }
}

