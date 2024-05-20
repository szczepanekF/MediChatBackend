package pl.logic.site.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pl.logic.site.model.enums.EmailType;
import pl.logic.site.model.mysql.SpringUser;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender javaMailSender;
    //    private final static String EMAIL_FROM = "zzjp_password_recovery@gmail.com";

    private static String loadHtmlFile(EmailType emailType) throws IOException {
        String template_name = "";
        switch (emailType) {
            case RESET_PASSWORD -> template_name = "_reset_password_mail_template.html";
            case DIAGNOSIS_REQUEST -> template_name = "_diagnosis_request_info_template.html";
        }
        String template_path = "C:\\Users\\jkowa\\Desktop\\Studia\\Semestr VI\\Project\\ZZJPBackend\\src\\main\\java\\pl\\logic\\site\\mails\\" + template_name;
        return new String(Files.readAllBytes(Paths.get(template_path)));
    }

    private static String getCompletedHtmlContent(Map<String, String> placeholders, EmailType emailType) throws IOException {
        String htmlContent = loadHtmlFile(emailType);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return htmlContent;
    }
//    private static Map<String, String> createPlaceholdersMap(String username, String token) {
//        Map<String, String> placeholders = new HashMap<>();
//        placeholders.put("name", username);
//        placeholders.put("token", token);
//        return placeholders;
//    }
    public void sendEmail(EmailType emailType, Map<String, String> params) throws IOException, MessagingException {
        params.put("appname", "Medi_ZZJP");

        String htmlContent = getCompletedHtmlContent(params, emailType);
        System.out.print(htmlContent);
        String subject = "RESET PASSWORD";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(params.get("emailAddress"));
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        javaMailSender.send(message);
    }
}
