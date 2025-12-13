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
 * Entity Profile theo bảng profiles trong database
 * @author ADMIN
 */
@ToString
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Profile {
    private Integer id;
    private Integer userId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String avatar;
    private Date birthdate;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Thông tin từ bảng users và roles (để hiển thị)
    private String username;
    private String roleName;
}



