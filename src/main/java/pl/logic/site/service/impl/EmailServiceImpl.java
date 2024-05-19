package pl.logic.site.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {
    @Autowired
    private JavaMailSender javaMailSender;
//    private final static String EMAIL_FROM = "zzjp_password_recovery@gmail.com";


    public void sendEmail(String token, String toEmailAddress) {
        String body = "Your recovery token: " + token + "\nPaste it into the token section";
        String subject = "RESET PASSWORD";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(toEmailAddress);
        email.setFrom("kkowalik408@gmail.com");
        javaMailSender.send(email);
    }
}
