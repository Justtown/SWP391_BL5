package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Timestamp;

/**
 * Entity cho bảng password_reset_requests
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetRequest {
    private Integer id;
    private Integer userId;
    private String email;
    private Timestamp requestTime;
    private String status;
    private String newPassword;
    private Boolean passwordChanged;
    private Timestamp approvedTime; // Thời gian admin approve password reset
}

