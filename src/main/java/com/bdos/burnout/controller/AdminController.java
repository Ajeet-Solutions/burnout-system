package com.bdos.burnout.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    // Process admin login credentials
    @PostMapping("/admin-login")
    public String handleAdminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {

        if (username != null && password != null &&
                "admin".equals(username.trim()) && "admin123".equals(password.trim())) {

            // Store admin identity in session
            session.setAttribute("loggedInAdmin", "admin");
            return "redirect:/admin-dashboard";
        }

        // Redirect with error status if credentials fail
        return "redirect:/admin-login?error=true";
    }

    // Process admin logout request
    @PostMapping("/admin-logout")
    public String handleAdminLogout(HttpSession session) {
        // Clear session data and invalidate
        if (session != null) {
            session.removeAttribute("loggedInAdmin");
            session.invalidate();
        }
        return "redirect:/admin-login";
    }
}