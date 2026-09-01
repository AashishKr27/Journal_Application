package com.journal.journal.controller;

import com.journal.journal.api.response.WeatherResponse;
import com.journal.journal.entity.User;
import com.journal.journal.repository.UserRepository;
import com.journal.journal.service.UserService;
import com.journal.journal.service.WeatherService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WeatherService weatherService;

    private String concat(String first, String second) {
        return first + second;
    }

    /*public ResponseEntity<@NonNull Object> getUsers() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();
            return new ResponseEntity<>(userService.findByUsername(username), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>((HttpHeaders) null, HttpStatus.BAD_GATEWAY);
        }
    }*/

    @PutMapping()
    public ResponseEntity<@NonNull String> updateUser(@RequestBody User newUser) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User oldUser = userService.findByUsername(username);

            oldUser.setUsername(!newUser.getUsername().isEmpty() ? newUser.getUsername() : oldUser.getUsername());
            oldUser.setPassword(!newUser.getPassword().isEmpty() ? newUser.getPassword() : oldUser.getPassword());
            userService.saveNewUser(oldUser);

            return new ResponseEntity<>("User updated successfully", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update field", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping
    public ResponseEntity<@NonNull String> deleteAllUsers() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean flag = userRepository.deleteByUsername(auth.getName());
            if(flag) return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            return new ResponseEntity<>("Failed to delete user", HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("", HttpStatus.BAD_GATEWAY);
        }
    }

    @GetMapping("")
    public ResponseEntity<String> greetings() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        WeatherResponse weatherResponse = weatherService.getWeather("New Delhi");

        String weather =
                weatherResponse != null ? (
                        concat(
                                STR.", Temperature: \{weatherResponse.getCurrent().getTemperature()}",
                                STR.", Feels Like: \{weatherResponse.getCurrent().getFeelslike()}"
                            )
                        ) : "";

        return new ResponseEntity<>(STR."Hi \{user.getUsername()}\{weather}",
                HttpStatus.OK);
    }
}
