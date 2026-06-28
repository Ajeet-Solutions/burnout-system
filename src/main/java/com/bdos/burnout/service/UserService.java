package com.bdos.burnout.service;

import com.bdos.burnout.model.User;
import com.bdos.burnout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String saveUserToSystem(String name, String email, String rawPassword, String confirmPassword, MultipartFile profileImage) throws IOException {

        String cleanedEmail = email.trim().toLowerCase();

        // 1. Password validation
        if (!rawPassword.equals(confirmPassword)) {
            return "Error: Password and Confirm Password do not match!";
        }

        // 2. Email check
        if (userRepository.existsByEmail(cleanedEmail)) {
            return "Error: Email is already registered!";
        }

        // 3. Image Handling (Skip folder creation to avoid permission errors)
        String finalImageUrl = "/images/default.png";



        // 4. Save User
        String dummyHashedPassword = "SECURE_" + rawPassword.trim() + "_KEY";
        User newUser = new User(name, cleanedEmail, dummyHashedPassword, finalImageUrl);
        userRepository.save(newUser);

        return "Success";
    }

    @Transactional(readOnly = true)
    public boolean authenticateUser(String email, String rawPassword) {
        String cleanedEmail = email.trim().toLowerCase();
        java.util.Optional<User> userOptional = userRepository.findByEmail(cleanedEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String incomingHashedPassword = "SECURE_" + rawPassword.trim() + "_KEY";
            return user.getPassword().equals(incomingHashedPassword);
        }
        return false;
    }
}