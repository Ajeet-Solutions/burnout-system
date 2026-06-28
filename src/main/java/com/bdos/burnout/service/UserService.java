package com.bdos.burnout.service;

import com.bdos.burnout.model.User;
import com.bdos.burnout.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String saveUserToSystem(String name, String email, String rawPassword, String confirmPassword, MultipartFile profileImage) throws IOException {
        String cleanedEmail = email.trim().toLowerCase();

        if (!rawPassword.equals(confirmPassword)) return "Error: Password and Confirm Password do not match!";
        if (userRepository.existsByEmail(cleanedEmail)) return "Error: Email is already registered!";

        String finalImageUrl = "/uploads/default.png";

        // Image logic crash-proof kar diya hai
        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                String uniqueFileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
                Path rootPath = Paths.get(System.getProperty("user.dir"), "uploads");
                if (!Files.exists(rootPath)) Files.createDirectories(rootPath);
                Files.copy(profileImage.getInputStream(), rootPath.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
                finalImageUrl = "/uploads/" + uniqueFileName;
            }
        } catch (Exception e) {
            System.out.println("Image upload skipped: " + e.getMessage());
        }

        String hashedPassword = "SECURE_" + rawPassword.trim() + "_KEY";
        userRepository.save(new User(name, cleanedEmail, hashedPassword, finalImageUrl));
        return "Success";
    }

    @Transactional(readOnly = true)
    public boolean authenticateUser(String email, String rawPassword) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .map(user -> user.getPassword().equals("SECURE_" + rawPassword.trim() + "_KEY"))
                .orElse(false);
    }
}