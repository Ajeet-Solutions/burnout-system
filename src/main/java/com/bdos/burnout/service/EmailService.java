package com.bdos.burnout.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendAssessmentReport(String toEmail, int score, String status) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String cleanTargetEmail = toEmail.trim();

            helper.setFrom("ajeet.singh896036@gmail.com", "BDOS Portal");
            helper.setTo(cleanTargetEmail);
            helper.setSubject("Your Burnout Assessment Report - Result");

            // Simple styling to pass Gmail's spam filters easily
            String statusColor = "#28a745"; // Default green
            String adviceText = "";

            if (status.contains("Low")) {
                statusColor = "#28a745";
                adviceText = "Your stress level is perfectly under control. Maintain your current daily healthy routine.";
            } else if (status.contains("Medium")) {
                statusColor = "#ffc107";
                adviceText = "Your workload is increasing. Please start taking short regular breaks during your work hours.";
            } else if (status.contains("High")) {
                statusColor = "#dc3545";
                adviceText = "Your burnout level is very high. Please stop working immediately, take a short vacation, or connect with a mentor.";
            }

            // Clean, simple, and safe HTML template (No dark gradients that cause blocking)
            String htmlBody = "<div style='font-family: sans-serif; max-width: 550px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; color: #333;'>"
                    + "  <h2 style='color: #4A00E0; border-bottom: 2px solid #4A00E0; padding-bottom: 10px;'>BDOS Mental Health Report</h2>"
                    + "  <p>Hello,</p>"
                    + "  <p>Thank you for completing your assessment test. Here is the processed result from your session:</p>"
                    + "  "
                    + "  <div style='background-color: #f8f9fa; padding: 15px; margin: 20px 0; border-left: 4px solid " + statusColor + ";'>"
                    + "    <p style='margin: 5px 0;'><strong>Current Status:</strong> <span style='color: " + statusColor + "; font-weight: bold;'>" + status + "</span></p>"
                    + "    <p style='margin: 5px 0;'><strong>Total Score:</strong> " + score + " / 15</p>"
                    + "  </div>"
                    + "  "
                    + "  <p style='background-color: #f0f7ff; padding: 12px; border-radius: 4px; font-size: 14px;'><strong>Recommendation:</strong> " + adviceText + "</p>"
                    + "  "
                    + "  <hr style='border: 0; border-top: 1px solid #eee; margin-top: 30px;'>"
                    + "  <p style='font-size: 11px; color: #777; text-align: center;'>This is an automated system email. Please do not reply.</p>"
                    + "</div>";

            String plainTextFallback = "Your burnout score is " + score + " and status is: " + status;
            helper.setText(plainTextFallback, htmlBody);

            mailSender.send(message);
            System.out.println("[HTML Email Status] Success! Beautiful layout report sent to: " + cleanTargetEmail);
        } catch (Exception e) {
            System.out.println("[HTML Email Error] Failed to send structured mail: " + e.getMessage());
        }
    }
}