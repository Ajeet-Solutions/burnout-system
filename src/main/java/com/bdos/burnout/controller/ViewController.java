package com.bdos.burnout.controller;

import com.bdos.burnout.model.Assessment;
import com.bdos.burnout.model.User;
import com.bdos.burnout.repository.AssessmentRepository;
import com.bdos.burnout.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Optional;

@Controller
public class ViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AssessmentRepository assessmentRepository;

    // Helper method to automatically populate session user data across pages
    private void addSessionUserData(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail != null) {
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isPresent()) {
                model.addAttribute("currentUser", userOpt.get());
                model.addAttribute("isLoggedIn", true);
                return;
            }
        }
        model.addAttribute("isLoggedIn", false);
    }

    // 1. Landing Home Page
    @GetMapping({"/", "/index"})
    public String homePage(HttpSession session, Model model) {
        addSessionUserData(session, model);
        return "index";
    }

    // 2. User Login Page
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 3. User Registration Page
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    // 4. User Dashboard Portal (Now with Dynamic Analytics for Day 7)
    @GetMapping("/dashboard")
    public String dashboardPage(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/login";
        }

        // Populate navbar navigation bar user configurations
        addSessionUserData(session, model);

        // Fetch user submission history database logs ordered by newest records first
        List<Assessment> userHistory = assessmentRepository.findByUserEmailOrderByExecutionDateDesc(userEmail);

        // Initialization variables for tracking analysis details
        int totalTests = userHistory.size();
        int lastScore = 0;
        String lastStatus = "No Test Taken Yet";
        String lastStatusColor = "text-muted";

        // Evaluate state metrics parameters if records exist inside system database
        if (totalTests > 0) {
            Assessment latestTest = userHistory.get(0); // Index position 0 fetches the most recent entry
            lastScore = latestTest.getTotalScore();
            lastStatus = latestTest.getStatus();

            // Map design layout style variations to correspond with evaluation categories
            if (lastStatus.contains("Low")) {
                lastStatusColor = "text-success";
            } else if (lastStatus.contains("Medium")) {
                lastStatusColor = "text-warning";
            } else if (lastStatus.contains("High")) {
                lastStatusColor = "text-danger";
            }
        }

        // Bind processed analytics objects context tokens directly onto view layout
        model.addAttribute("totalTests", totalTests);
        model.addAttribute("lastScore", lastScore);
        model.addAttribute("lastStatus", lastStatus);
        model.addAttribute("lastStatusColor", lastStatusColor);

        return "dashboard";
    }

    // 5. Assessment Instructions Page with Session Security
    @GetMapping("/instructions")
    public String instructionsPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        addSessionUserData(session, model);
        return "instructions";
    }

    // 6. Main Assessment Test Page with Session Security
    @GetMapping("/assessment")
    public String assessmentPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        addSessionUserData(session, model);
        return "assessment";
    }

    // 7. History Page (Fetching Live DB Data for Day 6)
    @GetMapping("/history")
    public String historyPage(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");
        if (userEmail == null) {
            return "redirect:/login";
        }

        addSessionUserData(session, model);

        List<Assessment> userHistory = assessmentRepository.findByUserEmailOrderByExecutionDateDesc(userEmail);
        model.addAttribute("historyList", userHistory);

        return "history";
    }

    // 8. Recommendations Page (Updated with Simple English Comments for Day 7)
    @GetMapping("/recommendations")
    public String recommendationsPage(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");

        // Redirect to login if user session is not found
        if (userEmail == null) {
            return "redirect:/login";
        }

        // Load user name and avatar profile image data
        addSessionUserData(session, model);

        // Fetch user assessment records from database sorted by latest first
        List<Assessment> userHistory = assessmentRepository.findByUserEmailOrderByExecutionDateDesc(userEmail);

        int currentScore = 0;
        String conditionLevel = "No Test Found";

        // Check if history is not empty and get the most recent test record
        if (!userHistory.isEmpty()) {
            Assessment latestRecord = userHistory.get(0);
            currentScore = latestRecord.getTotalScore();
            conditionLevel = latestRecord.getStatus();
        }

        // Pass calculated variables data directly to the Thymeleaf page
        model.addAttribute("score", currentScore);
        model.addAttribute("condition", conditionLevel);

        return "recommendations";
    }

    // 9. Email Mock Report Page
    @GetMapping("/email-mock")
    public String emailMockPage(HttpSession session, Model model) {
        String userEmail = (String) session.getAttribute("loggedInUser");

        // Redirect to login if user session is not found
        if (userEmail == null) {
            return "redirect:/login";
        }

        // Load user name and avatar profile image data for sidebar/navbar
        addSessionUserData(session, model);

        // Fetch user assessment records from database sorted by latest first
        List<Assessment> userHistory = assessmentRepository.findByUserEmailOrderByExecutionDateDesc(userEmail);

        int currentScore = 0;
        String conditionLevel = "No Test Found";
        String testDate = "Not Available";

        // Check if history is not empty and get the most recent test record details
        if (!userHistory.isEmpty()) {
            Assessment latestRecord = userHistory.get(0);
            currentScore = latestRecord.getTotalScore();
            conditionLevel = latestRecord.getStatus();

            // Format execution date cleanly (Removing T and trailing milliseconds)
            if (latestRecord.getExecutionDate() != null) {
                try {
                    java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    testDate = latestRecord.getExecutionDate().format(formatter);
                } catch (Exception e) {
                    // Fallback agar format mein koi issue aaye
                    testDate = latestRecord.getExecutionDate().toString().replace("T", " ");
                }
            }
        }

        // Pass analytical parameters directly to the Thymeleaf template view context
        model.addAttribute("score", currentScore);
        model.addAttribute("condition", conditionLevel);
        model.addAttribute("testDate", testDate);

        return "email-mock";
    }

    // 10. Admin Login Page
    @GetMapping("/admin-login")
    public String adminLoginPage(HttpSession session) {
        // Check if admin is already logged in, then send directly to admin dashboard
        if (session.getAttribute("loggedInAdmin") != null) {
            return "redirect:/admin-dashboard";
        }
        // If not logged in, show the admin login page
        return "admin-login";
    }

    // 11. Admin Dashboard Portal (Updated with Day 10 Live Monitoring Table Feed)
    @GetMapping("/admin-dashboard")
    public String adminDashboardPage(HttpSession session, Model model) {
        // Security Check: If admin session is empty, redirect back to login page
        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/admin-login";
        }

        // Fetch total count of users and tests from the database
        long totalRegisteredUsers = userRepository.count();
        long totalAssessmentsTaken = assessmentRepository.count();

        //  Fetch all system records sorted by execution date
        List<Assessment> allRecentTests = assessmentRepository.findAllByOrderByExecutionDateDesc();

        // Send counts and live list data to the Thymeleaf HTML page using model
        model.addAttribute("totalUsers", totalRegisteredUsers);
        model.addAttribute("totalTests", totalAssessmentsTaken);
        model.addAttribute("allTestsList", allRecentTests); // Live feed data token

        // Open the admin dashboard page
        return "admin-dashboard";
    }
}