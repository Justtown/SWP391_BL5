/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.argomachinemanagement.src.entity;

public class Account {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String fullName;
    private Boolean gender;
    private String avatar;
    private Boolean isActive;
    private Integer roleId;

    public Account() {
    }

    public Account(Integer id, String username, String password, String email, String phone, 
                   String fullName, Boolean gender, String avatar, Boolean isActive, Integer roleId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.fullName = fullName;
        this.gender = gender;
        this.avatar = avatar;
        this.isActive = isActive;
        this.roleId = roleId;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    // Builder pattern (manual implementation)
    public static AccountBuilder builder() {
        return new AccountBuilder();
    }

    public static class AccountBuilder {
        private Integer id;
        private String username;
        private String password;
        private String email;
        private String phone;
        private String fullName;
        private Boolean gender;
        private String avatar;
        private Boolean isActive;
        private Integer roleId;

        public AccountBuilder id(Integer id) {
            this.id = id;
            return this;
        }

        public AccountBuilder username(String username) {
            this.username = username;
            return this;
        }

        public AccountBuilder password(String password) {
            this.password = password;
            return this;
        }

        public AccountBuilder email(String email) {
            this.email = email;
            return this;
        }

        public AccountBuilder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public AccountBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public AccountBuilder gender(Boolean gender) {
            this.gender = gender;
            return this;
        }

        public AccountBuilder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public AccountBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public AccountBuilder roleId(Integer roleId) {
            this.roleId = roleId;
            return this;
        }

        public Account build() {
            return new Account(id, username, password, email, phone, fullName, gender, avatar, isActive, roleId);
        }
    }
}
