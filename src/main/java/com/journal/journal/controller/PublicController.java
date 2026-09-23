package com.journal.journal.controller;

import com.journal.journal.entity.User;
import com.journal.journal.service.UserService;
import com.journal.journal.utils.JWTUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JWTUtils jwtUtils;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<@NonNull String> signUp(@RequestBody User user) {
        try {

            System.out.println("Username received: " + user.getUsername());
            System.out.println("Password received: " + user.getPassword());

            if (user != null) {
                userService.saveNewUser(user);
                return new ResponseEntity<>("User created successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Failed to create user", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/log-in")
    public ResponseEntity<@NonNull String> logIn(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String jwt = jwtUtils.generateToken(user.getUsername());
            return new ResponseEntity<>(jwt,  HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred while createAuthenticationToken: ", e);
            return new ResponseEntity<>("Incorrect username or password", HttpStatus.BAD_REQUEST);
        }
    }
}
