package com.example.securityjwt.controller.dto;

import lombok.Data;

@Data
public class RoleToUserForm {
    private String email;
    private String roleName;
}
