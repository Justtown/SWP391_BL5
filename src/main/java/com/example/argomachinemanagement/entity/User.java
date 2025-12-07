package com.example.argomachinemanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Entity User theo bảng users trong database
 * @author ADMIN
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    private Integer id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private Integer status; // 1: active, 0: inactive
    private Timestamp createdAt;
    private String phoneNumber;
    private String address;
    private Date birthdate;
}

