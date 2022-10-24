package com.example.securityjwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.securityjwt.controller.dto.RoleToUserForm;
import com.example.securityjwt.domain.AppUser;
import com.example.securityjwt.domain.Role;
import com.example.securityjwt.service.AppUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@AllArgsConstructor
@RestController
@Slf4j
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping("/api/user")
    public ResponseEntity<List<AppUser>> getAllUser(){
        return new ResponseEntity<>( appUserService.getAppUsers(), HttpStatus.OK);
    }

    @GetMapping("/api/user/{email}")
    public ResponseEntity<AppUser> getUserByEmail(@PathVariable("email") String email){
        return new ResponseEntity<>(appUserService.getAppUser(email), HttpStatus.OK);
    }

    @PostMapping("/api/user")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser){
        return new ResponseEntity<>(appUserService.saveAppUser(appUser), HttpStatus.OK);
    }
    @PostMapping("/api/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role){
        return new ResponseEntity<>(appUserService.saveRole(role), HttpStatus.OK);
    }


    @PostMapping("/api/role/addtouser")
    public ResponseEntity<?> addRoleToAppUser(@RequestBody RoleToUserForm form){
        appUserService.addRoleToAppUser(form.getEmail(), form.getRoleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader  = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm  = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String email = decodedJWT.getSubject();
                AppUser user = appUserService.getAppUser(email);

                String access_token = JWT.create()
                        .withSubject(user.getEmail())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000 ))
                        .withIssuer(request.getRequestURI().toString())
                        .withClaim("role", user.getRoles().stream()
                                .map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", refresh_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);



            }catch (Exception e){

                response.setHeader("error", e.getMessage());
                response.setStatus(FORBIDDEN.value());
                //response.sendError(FORBIDDEN.value());

                Map<String, String> error = new HashMap<>();
                error.put("error_message", e.getMessage());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        }else {
            throw  new RuntimeException("Refresh token is missing");
        }

    }
}
