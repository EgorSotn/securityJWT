package com.example.securityjwt.service;



import com.example.securityjwt.domain.AppUser;
import com.example.securityjwt.domain.Role;

import java.util.List;

public interface AppUserService {
    AppUser saveAppUser(AppUser appUser);
    Role saveRole(Role role);
    void addRoleToAppUser(String email, String roleName);
    AppUser getAppUser(String email);
    List<AppUser> getAppUsers();
}
