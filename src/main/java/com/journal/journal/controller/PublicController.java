package com.journal.journal.controller;

import com.journal.journal.entity.User;
import com.journal.journal.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/create-user")
    public ResponseEntity<@NonNull String> saveUser(@RequestBody User user) {
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
}
