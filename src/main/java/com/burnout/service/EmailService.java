package com.burnout.service;

import com.burnout.model.CopingStrategy;
import com.burnout.repository.CopingStrategyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage; 
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.util.List;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CopingStrategyRepository copingStrategyRepo; //Added: To pick up dynamic cards from database

    //  BURNOUT REPORT MAIL (HTML Format - Updated with Profession, Age & Full Dynamic Cards)
    // FIXED parameters list to include profession and age dynamically
    public void sendBurnoutReport(String toEmail, String userName, String profession, int age, String status, int score) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Your Burnout Assessment Report - Burnout System");

            // DATABASE DYNAMIC FETCH: Status ke base par strategies pull karein
            List<CopingStrategy> strategies = copingStrategyRepo.findByBurnoutLevel(status);

            StringBuilder tipsHtml = new StringBuilder();
            StringBuilder stepsHtml = new StringBuilder();

            if (strategies != null && !strategies.isEmpty()) {
                for (int i = 0; i < strategies.size(); i++) {
                    CopingStrategy strat = strategies.get(i);
                    
                    // Safe string extractions to handle potential database null values safely
                    String title = strat.getTitle() != null ? strat.getTitle() : "Strategy Node";
                    String desc = strat.getDescription() != null ? strat.getDescription() : "";
                    String type = strat.getContentType() != null ? strat.getContentType() : "General";
                    String url = strat.getResourceUrl();

                    String block = "<div style='margin-bottom: 15px; padding-bottom: 10px; border-bottom: 1px dashed #ddd; text-align: left;'>"
                                 + "  <strong style='color: #2f3640; font-size: 14px;'>📌 " + title + "</strong>"
                                 + "  <p style='margin: 5px 0; color: #555; font-size: 13px;'>" + desc + "</p>"
                                 + "  <span style='background: #2f3640; color: white; padding: 2px 6px; font-size: 11px; border-radius: 4px;'>" + type + "</span>"
                                 + (url != null && !url.trim().isEmpty() ? "<br><a href='" + url + "' target='_blank' style='color: #fda085; font-size: 12px; font-weight: bold; text-decoration: none; display: inline-block; mt-1;'>View Resource →</a>" : "")
                                 + "</div>";

                    if (i % 2 == 0) {
                        tipsHtml.append(block);
                    } else {
                        stepsHtml.append(block);
                    }
                }
            }

            // Fallback content checking frames
            if (tipsHtml.length() == 0) {
                tipsHtml.append("<p style='color: #777; font-size: 13px;'>Take regular short breaks during your tasks.</p>");
            }
            if (stepsHtml.length() == 0) {
                stepsHtml.append("<div style='text-align:left;'><strong style='color:#2f3640; font-size:14px;'>📌 Routine Checkup</strong>"
                               + "<p style='margin:5px 0; color:#555; font-size:13px;'>Keep monitoring your daily stress levels regularly.</p></div>");
            }

            // Status Badge Border Accent Colors Mapping
            String statusColor = "#11998e"; // Low Risk
            if (status.equalsIgnoreCase("High Risk")) statusColor = "#cb2d3e";
            else if (status.equalsIgnoreCase("Medium Risk")) statusColor = "#ff9900";

            // Rich HTML Structure Generation Block
            String htmlContent = "<html><body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6; background-color: #f4f6f9; padding: 20px;'>"
                    + "<div style='max-width: 650px; margin: 0 auto; background: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); border: 1px solid #eee;'>"
                    + "  <div style='background: linear-gradient(135deg, #2f3640, #1c2129); color: white; padding: 30px 20px; text-align: center;'>"
                    + "    <h2 style='margin: 0; font-size: 24px; letter-spacing: 1px;'>Burnout System Report</h2>"
                    + "    <p style='margin: 5px 0 0 0; color: #fda085; font-size: 14px;'>Personalized Health & Stress Analysis</p>"
                    + "  </div>"
                    + "  <div style='padding: 25px;'>"
                    + "    <p style='font-size: 16px; margin-top: 0;'>Hello <strong>" + userName + "</strong>,</p>"
                    + "    <p style='color: #555;'>Thank you for taking the Burnout Assessment. Based on your metrics, here is your structural health profile:</p>"
                    
                    // User info block containing age and profession metrics
                    + "    <div style='background-color: #f8f9fa; border-left: 4px solid #2f3640; padding: 15px; border-radius: 4px; margin: 20px 0; text-align: left;'>"
                    + "      <table style='width: 100%; font-size: 14px; color: #444; border-collapse: collapse;'>"
                    + "        <tr><td style='padding: 4px 0;'><strong>Email:</strong> " + toEmail + "</td><td style='padding: 4px 0;'><strong>Profession:</strong> " + profession + "</td></tr>"
                    + "        <tr><td style='padding: 4px 0;'><strong>Age:</strong> " + age + " Yrs</td><td style='padding: 4px 0;'><strong>Total Score:</strong> " + score + " / 6</td></tr>"
                    + "      </table>"
                    + "    </div>"
                    
                    // Final Calculated Risk Threshold Card Container
                    + "    <div style='background: #fff; border: 1px solid #eef2f3; border-radius: 8px; padding: 15px; margin-bottom: 25px; text-align: center; box-shadow: 0 2px 8px rgba(0,0,0,0.02);'>"
                    + "      <h4 style='margin: 0 0 5px 0; color: #666; font-size: 14px;'>Current Mental Health Status</h4>"
                    + "      <h2 style='margin: 5px 0; color: " + statusColor + "; font-size: 28px;'>" + status + "</h2>"
                    + "    </div>"

                    // Parallel Web Layout Structure - Two HTML Email Content Boxes Table Representation
                    + "    <table style='width: 100%; table-layout: fixed; border-collapse: separate; border-spacing: 12px 0;'>"
                    + "      <tr>"
                    + "        <td style='background: #ffffff; border: 1px solid #eef2f3; border-top: 4px solid " + statusColor + "; padding: 15px; border-radius: 6px; vertical-align: top; box-shadow: 0 3px 10px rgba(0,0,0,0.03);'>"
                    + "          <h4 style='color: " + statusColor + "; margin-top: 0; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; text-align: left;'>💡 Recommended Tips</h4>"
                    +            tipsHtml.toString()
                    + "        </td>"
                    + "        <td style='background: #ffffff; border: 1px solid #eef2f3; border-top: 4px solid " + statusColor + "; padding: 15px; border-radius: 6px; vertical-align: top; box-shadow: 0 3px 10px rgba(0,0,0,0.03);'>"
                    + "          <h4 style='color: " + statusColor + "; margin-top: 0; margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; text-align: left;'>📈 Next Steps for Growth</h4>"
                    +            stepsHtml.toString()
                    + "        </td>"
                    + "      </tr>"
                    + "    </table>"

                    + "    <hr style='border: 0; border-top: 1px solid #eee; margin: 25px 0;'>"
                    + "    <p style='font-size: 12px; color: #777; text-align: center;'>© 2026 Burnout Detection System | All Rights Reserved</p>"
                    + "    <p style='font-size: 12px; color: #999; text-align: center; margin-top: -10px;'>Developed by: <strong>Ajeet Singh</strong></p>"
                    + "  </div>"
                    + "</div>"
                    + "</body></html>";

            helper.setText(htmlContent, true); 
            mailSender.send(message);
            System.out.println("HTML Rich Cards Email successfully sent to: " + toEmail);

        } catch (Exception e) {
            System.err.println("Email transmission error: " + e.getMessage());
        }
    }

    // FORGOT PASSWORD MAIL (Plain Text Format - Completely Unchanged)
    // Original method completely left alone to protect your password recovery pipeline flow
    public void sendSimpleEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            
            message.setFrom("ajeetsingh.dev01@gmail.com"); 
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            
            mailSender.send(message);
            System.out.println("Forgot Password Log: Email sent successfully to " + toEmail);
        } catch (Exception e) {
            System.err.println("Mail Error: Failed to send temporary password. " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}