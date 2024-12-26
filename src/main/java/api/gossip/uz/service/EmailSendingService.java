package api.gossip.uz.service;

import api.gossip.uz.util.JwtUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailSendingService {
    @Value("${spring.mail.username}")
    String fromAccount;

    @Value("${server.domain}")
    String serverDomain;

    final JavaMailSender javaMailSender;

    public void sendRegistrationEmail(String mail, Integer profileId) {
        /*    */
        String subject = "Complete registration";
        String body = "Please click to link for completing to registration: %s/api/auths/registration/verification/%s";
        body = String.format(body, serverDomain, JwtUtil.encode(profileId));
        sendEmail(mail, subject, body);
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
