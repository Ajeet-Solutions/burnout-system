package com.burnout.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.burnout.model.User;
import com.burnout.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    // Process user registration with unique email validation
    public String register(User user) {
    
        Optional<User> existingUser = repo.findByEmail(user.getEmail());

        // Validate if the email identity already exists in the system
        if (existingUser.isPresent()) {
            return "Email already in use. Please try another one or login to your account."; 
        }

        user.setLoggedIn(false); 
        repo.save(user);

        // Return standardized success state flag to the frontend
        return "SUCCESS"; 
    }

    // Process user login credentials and session status updates
    public String login(String email, String password) {
        Optional<User> userOpt = repo.findByEmail(email);

        // Check if user record is present in the database
        if (!userOpt.isPresent()) {
            return "User profile not found. Please register first.";
        }

        User user = userOpt.get();

        // Validate plain text credentials with the current state
        if (!user.getPassword().equals(password)) {
            return "Invalid credentials provided.";
        }

        // Enforce single-session active check constraint
        if (user.isLoggedIn()) {
            return "Account session is already active.";
        }

        // Persist session lifecycle state transition to true
        user.setLoggedIn(true);
        repo.save(user);

        return "Success Authentication verified and session established.";
    }
    
    // Fetch user secure properties matching dynamic session tokens
    public User getUserDetails(String email) {
        Optional<User> userOpt = repo.findByEmail(email);
        
        // Conditional block to ensure data visibility parameters match session logs
        if (userOpt.isPresent() && userOpt.get().isLoggedIn()) {
            return userOpt.get(); 
        }
        // Return blank runtime references to bypass unauthorized requests
        return null; 
    }

    // Process secure session termination lifecycle
    public String logout(String email) {
        Optional<User> userOpt = repo.findByEmail(email);

        if (!userOpt.isPresent()) {
            return "Error: Profile mapping not found.";
        }

        User user = userOpt.get();

        // Validate current context before modifying database values
        if (!user.isLoggedIn()) {
            return "Session is already terminated.";
        }

        // Reset user session lifecycle data parameters back to false
        user.setLoggedIn(false);
        repo.save(user);

        return "Success: Session terminated. Login required to view protected profile resources.";
    }
}