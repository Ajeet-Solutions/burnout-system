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

    // Fixed: Using relative path to work on both Windows and Linux (Render)
    private final String UPLOAD_DIR = "uploads/";

    @Transactional
    public String saveUserToSystem(String name, String email, String rawPassword, String confirmPassword, MultipartFile profileImage) throws IOException {

        String cleanedEmail = email.trim().toLowerCase();

        if (!rawPassword.equals(confirmPassword)) {
            return "Error: Password and Confirm Password do not match!";
        }

        if (userRepository.existsByEmail(cleanedEmail)) {
            return "Error: Email is already registered!";
        }

        String finalImageUrl = "/uploads/default.png";

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                String uniqueFileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();

                // Get current working directory (works on Render)
                Path rootPath = Paths.get(System.getProperty("user.dir"), UPLOAD_DIR);
                if (!Files.exists(rootPath)) {
                    Files.createDirectories(rootPath);
                }

                Files.copy(profileImage.getInputStream(), rootPath.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
                finalImageUrl = "/uploads/" + uniqueFileName;
            }
        } catch (Exception e) {
            System.out.println("Error saving image: " + e.getMessage());
            // Even if image fails, we continue with default image
        }

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

            // Password match check
            return user.getPassword().equals(incomingHashedPassword);
        }
        return false;
    }
}