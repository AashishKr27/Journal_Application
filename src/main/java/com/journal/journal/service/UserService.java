package com.journal.journal.service;

import com.journal.journal.entity.User;
import com.journal.journal.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UserService {
    private static final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Autowired
    private UserRepository userRepository;

    public void saveNewUser(User user) throws BadRequestException {
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            user.setRoles(List.of("USER"));
            userRepository.save(user);
            log.info("User saved successfully");
        } catch (Exception e) {
            log.error("User save failed");
            throw new BadRequestException(e.getMessage());
        }
    }

    public void saveAdmin(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRoles(List.of("USER", "ADMIN"));
        userRepository.save(user);
    }

    public void saveUser(User user) throws BadRequestException {
        userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String _id) {
        return userRepository.findById(_id).orElse(null);
    }

    public void deleteAllUser() {
        userRepository.deleteAll();
    }

    public void deleteById(String _id) {
        userRepository.deleteById(_id);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
