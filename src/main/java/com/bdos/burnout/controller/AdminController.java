package com.bdos.burnout.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    // Handle Admin Login Form Submission
    @PostMapping("/admin-login")
    public String handleAdminLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session) {

        // Simple Check: If username is 'admin' and password is 'admin123'
        if ("admin".equals(username) && "admin123".equals(password)) {
            // Save admin token inside session storage
            session.setAttribute("loggedInAdmin", "admin");
            // Redirect to admin dashboard portal
            return "redirect:/admin-dashboard";
        }

        // If credentials are wrong, redirect back to login page with error
        return "redirect:/admin-login?error=true";
    }

    // Handle Admin Logout Action
    @PostMapping("/admin-logout")
    public String handleAdminLogout(HttpSession session) {
        // Destroy the admin session completely
        session.removeAttribute("loggedInAdmin");
        session.invalidate();
        // Redirect back to admin login page
        return "redirect:/admin-login";
    }
}