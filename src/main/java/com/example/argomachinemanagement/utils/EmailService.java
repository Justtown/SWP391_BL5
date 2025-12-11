package com.example.argomachinemanagement.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service để gửi email
 */
public class EmailService {
    
    private static final Logger logger = Logger.getLogger(EmailService.class.getName());
    
    // Cấu hình SMTP
    private static final String SMTP_HOST = System.getProperty("smtp.host", "smtp.gmail.com");
    private static final String SMTP_PORT = System.getProperty("smtp.port", "587");
    private static final String SMTP_USERNAME = System.getProperty("smtp.username", "dungdnhe186806@fpt.edu.vn");
    private static final String SMTP_PASSWORD = System.getProperty("smtp.password", "eted ryow wank empb");
    private static final String SMTP_FROM_EMAIL = System.getProperty("smtp.from.email", "dungdnhe186806@fpt.edu.vn");
    private static final String SMTP_FROM_NAME = System.getProperty("smtp.from.name", "Argo Machine Management");
    
    /**
     * Gửi email với mật khẩu mới
     */
    public static boolean sendPasswordResetEmail(String recipientEmail, String newPassword) {
        try {
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SMTP_HOST);
            properties.put("mail.smtp.port", SMTP_PORT);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.ssl.trust", SMTP_HOST);
            
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });
            
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SMTP_FROM_EMAIL, SMTP_FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Đặt lại mật khẩu - Argo Machine Management");
            
            String htmlContent = buildPasswordResetEmailContent(newPassword);
            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            Transport.send(message);
            logger.info("Password reset email sent successfully to: " + recipientEmail);
            return true;
            
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error sending password reset email to: " + recipientEmail, ex);
            System.err.println("Error sending email: " + ex.getMessage());
            return false;
        }
    }
    
    private static String buildPasswordResetEmailContent(String newPassword) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: #f9fafb; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".password-box { background: white; border: 2px solid #667eea; border-radius: 8px; padding: 15px; margin: 20px 0; text-align: center; }" +
                ".password { font-size: 24px; font-weight: bold; color: #667eea; letter-spacing: 2px; }" +
                ".warning { background: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; }" +
                ".footer { text-align: center; margin-top: 30px; color: #666; font-size: 12px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h2>Đặt lại mật khẩu thành công</h2>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Xin chào,</p>" +
                "<p>Yêu cầu đặt lại mật khẩu của bạn đã được phê duyệt. Dưới đây là mật khẩu mới của bạn:</p>" +
                "<div class='password-box'>" +
                "<div class='password'>" + newPassword + "</div>" +
                "</div>" +
                "<div class='warning'>" +
                "<strong>⚠️ Lưu ý quan trọng:</strong><br>" +
                "Sau khi đăng nhập với mật khẩu mới này, bạn sẽ được yêu cầu đổi mật khẩu ngay lập tức để bảo mật tài khoản." +
                "</div>" +
                "<p>Bạn có thể đăng nhập ngay tại hệ thống.</p>" +
                "<p>Trân trọng,<br>Đội ngũ Argo Machine Management</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Email này được gửi tự động, vui lòng không trả lời email này.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

