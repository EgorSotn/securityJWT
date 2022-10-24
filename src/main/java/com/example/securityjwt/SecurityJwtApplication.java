package com.example.securityjwt;

import com.example.securityjwt.domain.AppUser;
import com.example.securityjwt.domain.Role;
import com.example.securityjwt.domain.StatusAppUser;
import com.example.securityjwt.service.AppUserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class SecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityJwtApplication.class, args);
    }
    @Bean
    CommandLineRunner run(AppUserService appUserService){
        return args -> {
            appUserService.saveRole(new Role(null, "ROLE_USER"));
            appUserService.saveRole(new Role(null, "ROLE_ADMIN"));

            appUserService.saveAppUser(new AppUser(null, "Egor", "email","1234", StatusAppUser.ONLINE, new ArrayList<>()));
            appUserService.saveAppUser(new AppUser(null, "Daniil", "email2","4321", StatusAppUser.ONLINE, new ArrayList<>()));

            appUserService.addRoleToAppUser("email", "ROLE_ADMIN");
            appUserService.addRoleToAppUser("email2", "ROLE_USER");
        };
    }

}
