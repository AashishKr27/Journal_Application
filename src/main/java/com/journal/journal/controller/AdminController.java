package com.journal.journal.controller;

import com.journal.journal.entity.User;
import com.journal.journal.service.UserService;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Data
class JustUsers {
    public String username;
    public List<String> roles;
}

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/all-users")
    public ResponseEntity<@NonNull Object> getAllUsers() {
        List<User> users = userService.getUsers();
        if(!users.isEmpty()) {
            List<JustUsers> justUsers = new ArrayList<>();
            for (User user : users) {
                JustUsers userJustUsers = new JustUsers();
                userJustUsers.username = user.getUsername();
                userJustUsers.roles = user.getRoles();
                justUsers.add(userJustUsers);
            } return new ResponseEntity<>(justUsers, HttpStatus.OK);
        }
        return new ResponseEntity<>("No users available", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/create-admin")
    public ResponseEntity<@NonNull Object> createAdmin(@RequestBody User user) {
        try {
            if(user != null) {
                if(user.getUsername().isEmpty() || user.getPassword().isEmpty()) {
                    return new ResponseEntity<>("Username or password cannot be empty", HttpStatus.BAD_REQUEST);
                } else {
                    userService.saveAdmin(user);
                    return new ResponseEntity<>("Admin created", HttpStatus.OK);
                }
            } return new ResponseEntity<>("Provided request entity is null", HttpStatus.BAD_REQUEST);

        } catch(Exception e) {
            return new ResponseEntity<>("An error occurred while creating user", HttpStatus.BAD_GATEWAY);
        }
    }
}
