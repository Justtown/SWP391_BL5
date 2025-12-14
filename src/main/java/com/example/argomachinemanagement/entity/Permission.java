package com.example.argomachinemanagement.entity;

import lombok.*;
import java.sql.Timestamp;

/**
 * Entity Permission theo bảng permissions trong database
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Permission {
    private Integer id;
    private String permissionName;
    private String description;
    private String urlPattern;
    private Timestamp createdAt;
}
