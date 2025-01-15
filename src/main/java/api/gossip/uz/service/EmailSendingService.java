package api.gossip.uz.service;

import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSendingService {

    @Value("${spring.mail.username}")
    String fromAccount;

    @Value("${server.domain}")
    String serverDomain;

    final JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String email, Integer profileId, AppLanguage language) {
        /*    */
        String subject = "Complete registration";
        String body = "Please click to link for completing to registration: %s/api/auths/registration/email-verification/%s/%s";
        body = String.format(body, serverDomain, JwtUtil.encode(profileId), language.name());
        sendEmail(email, subject, body);
    }

    private void sendMimeEmail(String email, String subject, String body) {
        try {
            MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);

            MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body);
            CompletableFuture.runAsync(() -> {
                javaMailSender.send(msg);
            });
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendEmail(String mail, String subject, String body) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAccount);
        simpleMailMessage.setTo(mail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }
}
