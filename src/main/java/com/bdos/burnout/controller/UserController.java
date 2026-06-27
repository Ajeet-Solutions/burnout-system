package com.bdos.burnout.controller;

import com.bdos.burnout.model.Assessment;
import com.bdos.burnout.repository.AssessmentRepository;
import com.bdos.burnout.service.UserService;
import com.bdos.burnout.service.EmailService;
import jakarta.servlet.http.Cookie; // Cookie clear karne ke liye import kiya
import jakarta.servlet.http.HttpServletRequest; // Request track karne ke liye import kiya
import jakarta.servlet.http.HttpServletResponse; // Response mein cookie bhejne ke liye import kiya
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private EmailService emailService;

    // Handle user registration
    @PostMapping("/register")
    public String handleRegistration(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("profileImage") MultipartFile profileImage) {

        try {
            String result = userService.saveUserToSystem(name, email, password, confirmPassword, profileImage);

            if (result.equals("Success")) {
                return "redirect:/login";
            } else {
                return "redirect:/register?error";
            }
        } catch (IOException e) {
            System.out.println("Registration error: " + e.getMessage());
            return "redirect:/register?serverError";
        }
    }

    // Handle user login and session creation
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session) {

        boolean isAuthenticated = userService.authenticateUser(email, password);

        if (isAuthenticated) {
            session.setAttribute("loggedInUser", email);
            System.out.println("[Session Status] Success! Session created for: " + email);
            return "redirect:/";
        } else {
            return "redirect:/login?error";
        }
    }

    // ⭐ PROBLEM 4 FIXED: Handle user logout, session validation and Cookie clearing
    @GetMapping("/logout")
    public String handleLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            System.out.println("[Session Status] User logged out successfully.");
        }

        // Browser ki purani login cookie ko poori tarah delete (clear) kiya
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 0 seconds life yani turant khatam
        response.addCookie(cookie);

        return "redirect:/login?logout";
    }

    // Save assessment form answers to HTTP session
    @PostMapping("/assessment-submit")
    public String handleAssessmentSubmit(
            @RequestParam("q1") String q1,
            @RequestParam("q2") String q2,
            @RequestParam("q3") String q3,
            @RequestParam("q4") String q4,
            @RequestParam("q5") String q5,
            HttpSession session) {

        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        session.setAttribute("q1_score", q1);
        session.setAttribute("q2_score", q2);
        session.setAttribute("q3_score", q3);
        session.setAttribute("q4_score", q4);
        session.setAttribute("q5_score", q5);

        return "redirect:/review";
    }

    // Display review page with dynamic text data
    @GetMapping("/review")
    public String showReviewPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        if (session.getAttribute("q1_score") == null) {
            return "redirect:/assessment";
        }

        String q1 = (String) session.getAttribute("q1_score");
        String q2 = (String) session.getAttribute("q2_score");
        String q3 = (String) session.getAttribute("q3_score");
        String q4 = (String) session.getAttribute("q4_score");
        String q5 = (String) session.getAttribute("q5_score");

        model.addAttribute("ans1", convertValueToText(q1));
        model.addAttribute("ans2", convertValueToText(q2));
        model.addAttribute("ans3", convertValueToText(q3));
        model.addAttribute("ans4", convertValueToText(q4));
        model.addAttribute("ans5", convertValueToText(q5));

        return "review";
    }

    // Calculate dynamic burnout scores, save to database, and show results
    @GetMapping("/result")
    public String showResultPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        if (session.getAttribute("q1_score") == null) {
            return "redirect:/assessment";
        }

        String userEmail = (String) session.getAttribute("loggedInUser");

        int score1 = Integer.parseInt((String) session.getAttribute("q1_score"));
        int score2 = Integer.parseInt((String) session.getAttribute("q2_score"));
        int score3 = Integer.parseInt((String) session.getAttribute("q3_score"));
        int score4 = Integer.parseInt((String) session.getAttribute("q4_score"));
        int score5 = Integer.parseInt((String) session.getAttribute("q5_score"));

        int totalScore = score1 + score2 + score3 + score4 + score5;

        String burnoutStatus;
        String statusColor;
        String advice;

        if (totalScore <= 5) {
            burnoutStatus = "Low Burnout (Safe)";
            statusColor = "text-success";
            advice = "Your stress level is perfectly under control. Maintain your current daily healthy routine.";
        } else if (totalScore <= 10) {
            burnoutStatus = "Medium Burnout (Warning)";
            statusColor = "text-warning";
            advice = "Your workload is increasing. Please start taking short regular breaks during your work hours.";
        } else {
            burnoutStatus = "High Burnout (Danger)";
            statusColor = "text-danger";
            advice = "Your burnout level is very high. Please stop working immediately, take a short vacation, or connect with a mentor.";
        }

        // Persistence Layer Integration
        try {
            Assessment currentTest = new Assessment(userEmail, totalScore, burnoutStatus, LocalDateTime.now());
            assessmentRepository.save(currentTest);
            System.out.println("[Database Status] Assessment record saved successfully for: " + userEmail);

            // ⭐ PROBLEM 3 FIXED: Try-Catch safe wrapper block for Email
            // Agar email authentication fail bhi ho, toh user ka page hang nahi hoga aur results turant dikhenge!
            try {
                emailService.sendAssessmentReport(userEmail, totalScore, burnoutStatus);
                System.out.println("[Email Status] Request sent to email service.");
            } catch (Exception mailEx) {
                System.out.println("[HTML Email Error] Failed to send structured mail: " + mailEx.getMessage());
            }

        } catch (Exception e) {
            System.out.println("[Database Error] Could not save record: " + e.getMessage());
        }

        // Clear temporary assessment session attributes
        session.removeAttribute("q1_score");
        session.removeAttribute("q2_score");
        session.removeAttribute("q3_score");
        session.removeAttribute("q4_score");
        session.removeAttribute("q5_score");

        model.addAttribute("totalScore", totalScore);
        model.addAttribute("status", burnoutStatus);
        model.addAttribute("colorClass", statusColor);
        model.addAttribute("advice", advice);

        return "result";
    }

    private String convertValueToText(String val) {
        if (val == null) return "Never";
        return switch (val) {
            case "0" -> "Never";
            case "1" -> "Sometimes";
            case "2" -> "Often";
            case "3" -> "Always";
            default -> "Never";
        };
    }
}