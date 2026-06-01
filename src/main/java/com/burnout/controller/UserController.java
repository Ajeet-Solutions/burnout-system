package com.burnout.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.burnout.model.User;
import com.burnout.repository.UserRepository; 
import com.burnout.service.UserService;
import com.burnout.service.EmailService; 
import com.burnout.util.PasswordUtil; 
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*") 
public class UserController {

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepo; 

    @Autowired
    private EmailService emailService; 

    // REGISTER Encrypt user password and save account details
    @PostMapping("/register")
    public String register(@RequestBody User user){
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return "Error: Password cannot be empty!";
        }
        
        // Convert raw password into secure SHA-256 hash
        String securePassword = PasswordUtil.hashPassword(user.getPassword());
        user.setPassword(securePassword); 
        
        return service.register(user); 
    }

    // LOGIN Supports both Form-Data (RequestParam) and JSON (RequestBody) payloads
    @PostMapping("/login")
    public String login(@RequestParam(required = false) String email, 
                        @RequestParam(required = false) String password,
                        @RequestBody(required = false) User bodyUser) {
        
        String finalEmail = "";
        String finalPassword = "";

        // Process payload if frontend transmits data as JSON body
        if (bodyUser != null && bodyUser.getEmail() != null) {
            finalEmail = bodyUser.getEmail().trim();
            finalPassword = bodyUser.getPassword();
        } 
        // Process payload if frontend transmits data as URL Query Parameters
        else if (email != null && password != null) {
            finalEmail = email.trim();
            finalPassword = password;
        }

        // Validate parameter presence
        if (finalEmail.isEmpty() || finalPassword == null || finalPassword.isEmpty()) {
            return "Error: Email and Password are required fields!";
        }

        // Verify email registration status directly from database
        Optional<User> userOptional = userRepo.findByEmail(finalEmail);
        
        if (!userOptional.isPresent()) {
            System.out.println("❌ Auth Log: Target email record not found -> " + finalEmail);
            return "Error: This Email ID is not registered!";
        }

        User dbUser = userOptional.get();

        // Security Verification Compare plain text input against system cryptographic hash
        boolean isMatched = PasswordUtil.verifyPassword(finalPassword, dbUser.getPassword());
        
        if (isMatched) {
            System.out.println("Auth Log: Authentication successful for -> " + finalEmail);
            return service.login(finalEmail, dbUser.getPassword());
        } else {
            System.out.println("Auth Log: Password verification mismatch -> " + finalEmail);
            return "Error: Invalid Password! Please try again.";
        }
    }
    
    // FORGOT PASSWORD Generates an automated temporary credential and dispatches via email
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return "Error: Please enter your registered Email ID!";
        }

        // Verify user existence inside persistent storage
        Optional<User> userOptional = userRepo.findByEmail(email.trim());
        if (!userOptional.isPresent()) {
            return "Error: This Email ID is not registered in our system!";
        }

        User user = userOptional.get();

        //  Compute a randomized unique 6-digit transient security token
        String tempPassword = "TEMP" + (int)(Math.random() * 90000 + 10000);

        // Encrypt transient token using system standard secure hashing algorithms
        String hashedTempPassword = PasswordUtil.hashPassword(tempPassword);
        user.setPassword(hashedTempPassword);

        //  Update data state in persistent repository layer
        userRepo.save(user);

        //  Build automated notification message body template
        String emailBody = "Dear " + user.getName() + ",\n\n"
                + "We received a request to reset your password for the Burnout Detection System.\n"
                + "Your temporary login password is: " + tempPassword + "\n\n"
                + "Please use this temporary password to log in, and ensure you update your password from the dashboard immediately.\n\n"
                + "Best Regards,\nBurnout System Support Team";

        try {
            // Forward processing control to integrated communication layer module
            emailService.sendSimpleEmail(user.getEmail(), "Burnout System - Temporary Password Reset", emailBody);
            return "Success: A temporary password has been sent to your email address. Please check your inbox!";
        } catch (Exception e) {
            return "Error: Failed to dispatch email template. Please try again after some time.";
        }
    }
    
    @PostMapping("/logout")
    public String logout(@RequestParam String email) {
        return service.logout(email);
    }
    
    @GetMapping("/details")
    public User getUserDetails(@RequestParam String email) {
        Optional<User> userOptional = userRepo.findByEmail(email.trim());
        return userOptional.orElseGet(() -> service.getUserDetails(email));
    }
}