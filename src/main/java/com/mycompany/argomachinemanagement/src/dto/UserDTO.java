package com.mycompany.argomachinemanagement.src.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for displaying user list with role information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String roleName;
    private Integer status; // 1 = Active, 0 = Deactive
}

