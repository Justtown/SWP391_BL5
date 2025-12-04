package com.mycompany.argomachinemanagement.src.dto;

/**
 * DTO class for displaying user list with role information
 */
public class UserDTO {
    private Integer id;
    private String username;
    private String fullName;
    private String email;
    private String roleName;
    private Integer status; // 1 = Active, 0 = Deactive

    public UserDTO() {
    }

    public UserDTO(Integer id, String username, String fullName, String email, String roleName, Integer status) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    // Builder pattern
    public static UserDTOBuilder builder() {
        return new UserDTOBuilder();
    }

    public static class UserDTOBuilder {
        private Integer id;
        private String username;
        private String fullName;
        private String email;
        private String roleName;
        private Integer status;

        public UserDTOBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public UserDTOBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserDTOBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public UserDTOBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDTOBuilder roleName(String roleName) {
            this.roleName = roleName;
            return this;
        }

        public UserDTOBuilder status(Integer status) {
            this.status = status;
            return this;
        }

        public UserDTO build() {
            return new UserDTO(id, username, fullName, email, roleName, status);
        }
    }
}
