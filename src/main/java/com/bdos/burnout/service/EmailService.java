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

            helper.setFrom("no-reply@bdos.com");
            helper.setTo(toEmail);
            helper.setSubject("BDOS - Your Personal Burnout Assessment Report");

            // Dynamic Color Selection based on Burnout Status
            String badgeColor = "#6c757d"; // Default gray
            String adviceText = "";

            if (status.contains("Low")) {
                badgeColor = "#28a745"; // Success Green
                adviceText = "Your stress level is perfectly under control. Maintain your current daily healthy routine.";
            } else if (status.contains("Medium")) {
                badgeColor = "#ffc107"; // Warning Yellow
                adviceText = "Your workload is increasing. Please start taking short regular breaks during your work hours.";
            } else if (status.contains("High")) {
                badgeColor = "#dc3545"; // Danger Red
                adviceText = "Your burnout level is very high. Please stop working immediately, take a short vacation, or connect with a mentor.";
            }

            // Beautiful Professional HTML Template Design
            String htmlBody = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 10px; background-color: #fcfcfc;'>"
                    + "  <div style='text-align: center; background: linear-gradient(135deg, #4A00E0, #8E2DE2); padding: 20px; border-radius: 8px 8px 0 0; color: white;'>"
                    + "    <h2 style='margin: 0; font-size: 24px; letter-spacing: 1px;'>BDOS Wellness Portal</h2>"
                    + "    <p style='margin: 5px 0 0 0; font-size: 14px; opacity: 0.9;'>Corporate Burnout Detection System</p>"
                    + "  </div>"
                    + "  <div style='padding: 20px; background-color: white; border-radius: 0 0 8px 8px;'>"
                    + "    <p style='font-size: 16px; color: #333;'>Hello <strong>" + toEmail + "</strong>,</p>"
                    + "    <p style='color: #555; line-height: 1.5;'>Thank you for taking the mental health assessment test. Based on your inputs, our system analytics layer has processed your score report.</p>"
                    + "    <div style='margin: 25px 0; text-align: center; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border-left: 5px solid " + badgeColor + ";'>"
                    + "      <span style='font-size: 14px; text-transform: uppercase; color: #888; font-weight: bold; display: block; margin-bottom: 5px;'>Assessment Status</span>"
                    + "      <span style='background-color: " + badgeColor + "; color: " + (badgeColor.equals("#ffc107") ? "#333" : "white") + "; padding: 8px 16px; font-size: 18px; font-weight: bold; border-radius: 20px; display: inline-block; margin-bottom: 15px;'>" + status + "</span>"
                    + "      <div style='font-size: 28px; font-weight: bold; color: #333; margin-top: 5px;'>" + score + " <span style='font-size: 16px; color: #777;'>/ 15</span></div>"
                    + "    </div>"
                    + "    <table style='width: 100%; border-collapse: collapse; margin-top: 15px;'>"
                    + "      <thead>"
                    + "        <tr style='background-color: #f1f1f1; text-align: left;'>"
                    + "          <th style='padding: 10px; border: 1px solid #ddd; font-size: 14px;'>Metric Parameter</th>"
                    + "          <th style='padding: 10px; border: 1px solid #ddd; font-size: 14px;'>System Value</th>"
                    + "        </tr>"
                    + "      </thead>"
                    + "      <tbody>"
                    + "        <tr>"
                    + "          <td style='padding: 10px; border: 1px solid #ddd; font-size: 14px; color: #555;'>User Identity ID</td>"
                    + "          <td style='padding: 10px; border: 1px solid #ddd; font-size: 14px; font-weight: bold; color: #333;'>" + toEmail + "</td>"
                    + "        </tr>"
                    + "        <tr>"
                    + "          <td style='padding: 10px; border: 1px solid #ddd; font-size: 14px; color: #555;'>Calculated Aggregate Score</td>"
                    + "          <td style='padding: 10px; border: 1px solid #ddd; font-size: 14px; font-weight: bold; color: " + badgeColor + ";'>" + score + " out of 15</td>"
                    + "        </tr>"
                    + "      </tbody>"
                    + "    </table>"
                    + "    <div style='margin-top: 25px; padding: 15px; background-color: #f0f7ff; border-radius: 6px; border-left: 4px solid #0056b3;'>"
                    + "      <h4 style='margin: 0 0 5px 0; color: #0056b3; font-size: 15px;'>🎯 System Recommendation Action:</h4>"
                    + "      <p style='margin: 0; font-size: 14px; color: #333; line-height: 1.4;'>" + adviceText + "</p>"
                    + "    </div>"
                    + "    <hr style='border: 0; border-top: 1px solid #eee; margin: 30px 0;'>"
                    + "    <p style='font-size: 12px; color: #999; text-align: center; margin: 0;'>This is an automated report generated securely by your organization's BDOS engine. Please do not reply directly to this mail.</p>"
                    + "  </div>"
                    + "</div>";

            helper.setText(htmlBody, true); // true parameter sets content type as HTML
            mailSender.send(message);
            System.out.println("[HTML Email Status] Success! Beautiful layout report sent to: " + toEmail);
        } catch (Exception e) {
            System.out.println("[HTML Email Error] Failed to send structured mail: " + e.getMessage());
        }
    }
}