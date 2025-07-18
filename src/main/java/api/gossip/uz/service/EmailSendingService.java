package api.gossip.uz.service;

import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.util.JwtUtil;
import api.gossip.uz.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class EmailSendingService {


    @Value("${spring.mail.username}")
    private String fromAccount;

    @Value("${server.domain}")
    private String serverDomain;

    private final JavaMailSender javaMailSender;
    private final EmailHistoryService emailHistoryService;
    private final ResourceBundleService bundleService;

    public void sendRegistrationEmail(final String email, final Integer profileId, AppLanguage language) {
        /*    */
        final String subject = "Complete registration";
        String body = "Please click to link for completing to registration: %s/api/auths/registration/email-verification/%s?lang=%s";
        body = String.format(body, serverDomain, JwtUtil.encode(profileId), language.name());
        sendEmail(email, subject, body);
    }

    public void sendResetPasswordEmail(final String email, AppLanguage language) {
        final String subject = "Reset Password Confirmation";
        final String code = RandomUtil.getRandomSmsCode();
        final String body = "This is your confirm code for reset password " + code;
        checkAndSendMineEmail(email, subject, body, code);
    }

    public void sendUsernameChangeEmail(final String email, AppLanguage language) {
        final String subject = "username change confirmation";
        final String code = RandomUtil.getRandomSmsCode();
        final String body = "this is your confirm code for changing : " + code;
        checkAndSendMineEmail(email, subject, body, code);
    }

    protected void checkAndSendMineEmail(final String email, final String subject, final String body, final String code) {
        final Long emailLimit = 3L;
        //check
        final Long count = emailHistoryService.getEmailCount(email);
        if (count >= emailLimit) {
            System.out.println("---- Email limit reached. Email : " + email);
            throw ExceptionUtil.throwBadRequestException(bundleService.getMessage("sms.limit.reached"));
        }
        //send
        this.sendMimeEmail(email, subject, body);
        //create
        emailHistoryService.create(email, code, SmsType.RESET_PASSWORD);
    }

    protected void sendMimeEmail(final String email, final String subject, final String body) {
        try {
            final MimeMessage msg = javaMailSender.createMimeMessage();
            msg.setFrom(fromAccount);

            final MimeMessageHelper helper = new MimeMessageHelper(msg, true);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(body, true);
            CompletableFuture.runAsync(() -> javaMailSender.send(msg));
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendEmail(final String mail, final String subject, final String body) {
        final SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromAccount);
        simpleMailMessage.setTo(mail);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);
        javaMailSender.send(simpleMailMessage);
    }


}
