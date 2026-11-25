package com.resqlink.service;

import com.resqlink.model.User;
import com.resqlink.model.enums.UserRole;
import com.resqlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, String email, String phone, UserRole role, String organizationName) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(username, encodedPassword, fullName, email, phone, role, organizationName);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
