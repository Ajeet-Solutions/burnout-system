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

    private final String UPLOAD_DIRECTORY = "C:/burnout/uploads/";

    // Save newly registered user into the database
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
        if (profileImage != null && !profileImage.isEmpty()) {
            String uniqueFileName = System.currentTimeMillis() + "_" + profileImage.getOriginalFilename();
            Path folderPath = Paths.get(UPLOAD_DIRECTORY);

            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            Files.copy(profileImage.getInputStream(), folderPath.resolve(uniqueFileName), StandardCopyOption.REPLACE_EXISTING);
            finalImageUrl = "/uploads/" + uniqueFileName;
        }

        String dummyHashedPassword = "SECURE_" + rawPassword.trim() + "_KEY";

        User newUser = new User(name, cleanedEmail, dummyHashedPassword, finalImageUrl);
        userRepository.save(newUser);

        return "Success";
    }

    // Authenticate user credentials during login process
    @Transactional(readOnly = true)
    public boolean authenticateUser(String email, String rawPassword) {
        System.out.println("====== LOGIN DEBUG START ======");

        String cleanedEmail = email.trim().toLowerCase();
        System.out.println("Email from form: " + cleanedEmail);
        System.out.println("Password from form: " + rawPassword);

        java.util.Optional<User> userOptional = userRepository.findByEmail(cleanedEmail);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Status: User FOUND in database!");
            System.out.println("Saved password in DB: " + user.getPassword());

            String incomingHashedPassword = "SECURE_" + rawPassword.trim() + "_KEY";
            System.out.println("Hashed password from form: " + incomingHashedPassword);

            if (user.getPassword().equals(incomingHashedPassword)) {
                System.out.println("RESULT: Login Success! Password matched.");
                System.out.println("====== LOGIN DEBUG END ======");
                return true;
            } else {
                System.out.println("RESULT: Login Failed! Wrong password.");
            }
        } else {
            System.out.println("RESULT: Login Failed! Email not found in database.");
        }
        System.out.println("====== LOGIN DEBUG END ======");
        return false;
    }
}