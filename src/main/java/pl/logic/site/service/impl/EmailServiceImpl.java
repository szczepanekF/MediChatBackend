package pl.logic.site.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
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

    private static String loadHtmlFile() throws IOException {
        String reset_password_mail_template = "C:\\Users\\jkowa\\Desktop\\Studia\\Semestr VI\\Project\\ZZJPBackend\\src\\main\\java\\pl\\logic\\site\\mails\\_reset_password_mail_template.html";
        return new String(Files.readAllBytes(Paths.get(reset_password_mail_template)));
    }

    private static String getCompletedHtmlContent(Map<String, String> placeholders) throws IOException {
        String htmlContent = loadHtmlFile();
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            htmlContent = htmlContent.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return htmlContent;
    }
    private static Map<String, String> createPlaceholdersMap(String username, String token) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", username);
        placeholders.put("appname", "Medi_ZZJP");
        placeholders.put("token", token);
        return placeholders;
    }
    public void sendEmail(String token, String toEmailAddress, String username) throws IOException, MessagingException {
        Map<String, String> placeholders = createPlaceholdersMap(username, token);
        String htmlContent = getCompletedHtmlContent(placeholders);
        String subject = "RESET PASSWORD";

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(toEmailAddress);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);


        javaMailSender.send(message);
    }
}
