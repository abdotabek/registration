package api.gossip.uz.service;

import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.exception.BadRequestException;
import api.gossip.uz.util.JwtUtil;
import api.gossip.uz.util.RandomUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailSendingServiceTest {
    @Mock
    private JavaMailSender javaMailSender;
    @Mock
    private EmailHistoryService emailHistoryService;
    @Mock
    private ResourceBundleService bundleService;
    @InjectMocks
    private EmailSendingService emailSendingService;

    private Integer PROFILE_ID = 1;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailSendingService, "fromAccount", "test@from.com");
        ReflectionTestUtils.setField(emailSendingService, "serverDomain", "http://test.com");
    }


    @Test
    void sendRegistrationEmail_sendCorrectEmail() {
        String email = "998937877405";
        AppLanguage language = AppLanguage.EN;

        String expectedToken = JwtUtil.encode(PROFILE_ID);

        String expectedSubject = "Complete registration";
        String expectedBody = String.format(
                "Please click to link for completing to registration: %s/api/auths/registration/email-verification/%s?lang=%s",
                "http://test.com", expectedToken, language.name()
        );
        emailSendingService.sendRegistrationEmail(email, PROFILE_ID, language);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());


        SimpleMailMessage captorMessage = messageCaptor.getValue();
        assertEquals("test@from.com", captorMessage.getFrom());
        assertEquals(email, Objects.requireNonNull(captorMessage.getTo())[0]);
        assertEquals(expectedSubject, captorMessage.getSubject());
        assertEquals(expectedBody, captorMessage.getText());
    }


    @Test
    void sendResetPasswordEmail() throws MessagingException {
        String email = "test@from.com";
        AppLanguage language = AppLanguage.EN;
        String expectedSubject = "Reset Password Confirmation";
        String expectedCode = "123456";

        try (MockedStatic<RandomUtil> mockedStatic = mockStatic(RandomUtil.class)) {
            mockedStatic.when(RandomUtil::getRandomSmsCode).thenReturn(expectedCode);

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

            emailSendingService.sendResetPasswordEmail(email, language);

            verify(javaMailSender).send(mimeMessage);
            verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), eq(new InternetAddress(email)));
            verify(mimeMessage).setSubject(eq(expectedSubject));
            verify(mimeMessage).setContent(any(MimeMultipart.class));

            ArgumentCaptor<MimeMultipart> multipartCaptor = ArgumentCaptor.forClass(MimeMultipart.class);
            verify(mimeMessage).setContent(multipartCaptor.capture());
        }
    }

    @Test
    void sendUsernameChangeEmail() throws Exception {
        String email = "test@from.com";
        AppLanguage language = AppLanguage.EN;
        String expectedSubject = "username change confirmation";
        String expectedCode = "123456";

        try (MockedStatic<RandomUtil> mockedStatic = mockStatic(RandomUtil.class)) {
            mockedStatic.when(RandomUtil::getRandomSmsCode).thenReturn(expectedCode);

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

            emailSendingService.sendUsernameChangeEmail(email, language);

            verify(javaMailSender).send(mimeMessage);
            verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), eq(new InternetAddress(email)));
            verify(mimeMessage).setSubject(eq(expectedSubject));
            verify(mimeMessage).setContent(any(MimeMultipart.class));
        }
    }

    @Test
    void checkAndSendMineEmail_successfulSend() throws MessagingException {
        String email = "test@from.com";
        String expectedSubject = "Test subject";
        String expectedBody = "Body test";
        String expectedCode = "Test Code";

        when(emailHistoryService.getEmailCount(email)).thenReturn(1L);
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSendingService.checkAndSendMineEmail(email, expectedSubject, expectedBody, expectedCode);

        verify(javaMailSender).send(mimeMessage);
        verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), eq(new InternetAddress(email)));
        verify(mimeMessage).setSubject(eq(expectedSubject));
        verify(mimeMessage).setContent(any(MimeMultipart.class));
        verify(emailHistoryService).create(email, expectedCode, SmsType.RESET_PASSWORD);
        verify(bundleService, never()).getMessage(anyString());
    }

    @Test
    void checkAndSendMineEmail_limit_throwsException() {
        String email = "test@from";
        String expectedSubject = "Test subject";
        String expectedBody = "Body test";
        String expectedCode = "Test Code";

        when(emailHistoryService.getEmailCount(email)).thenReturn(3L);
        when(bundleService.getMessage("sms.limit.reached")).thenReturn("Sms limit reached");
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> emailSendingService.checkAndSendMineEmail(email, expectedSubject, expectedBody, expectedCode));
        assertEquals("Sms limit reached", exception.getMessage());

        verify(emailHistoryService).getEmailCount(email);
        verify(javaMailSender, never()).send(any(MimeMessage.class));
        verify(emailHistoryService, never()).create(anyString(), anyString(), any(SmsType.class));
        verify(bundleService).getMessage("sms.limit.reached");
    }

    @Test
    void sendMimeEmail_successfullySend() throws MessagingException {
        String email = "test@from.com";
        String subject = "Test subject";
        String body = "<h1> Test body <h1>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailSendingService.sendMimeEmail(email, subject, body);

        verify(javaMailSender, times(1)).createMimeMessage();
        verify(mimeMessage).setFrom("test@from.com");
        verify(javaMailSender, times(1)).send(eq(mimeMessage));

        verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), eq(new InternetAddress(email)));
        verify(mimeMessage).setSubject(eq(subject));
    }

    @Test
    void sendMimeEmail_messagingException() throws MessagingException {
        String email = "test@from.com";
        String subject = "Test subject";
        String body = "<h1> Test body <h1>";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MessagingException("Email sending failed")).when(mimeMessage).setFrom(any(String.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> emailSendingService.sendMimeEmail(email, subject, body));
        assertInstanceOf(MessagingException.class, exception.getCause());
        assertEquals("Email sending failed", exception.getCause().getMessage());
        verify(javaMailSender, never()).send(any(MimeMessage.class));
    }

    @Test
    void sendEmail_successfullySend() {
        String email = "test@from.com";
        String subject = "test subject";
        String body = "test body";

        emailSendingService.sendEmail(email, subject, body);

        ArgumentCaptor<SimpleMailMessage> argumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(argumentCaptor.capture());

        SimpleMailMessage captureMessage = argumentCaptor.getValue();
        assertEquals("test@from.com", captureMessage.getFrom());
        assertEquals("test subject", captureMessage.getSubject());
        assertEquals("test body", captureMessage.getText());
    }

    @Test
    void sendEmail_mailSenderException() {
        String email = "test@from.com";
        String subject = "test subject";
        String body = "test body";

        doThrow(new MailSendException("Email sending failed")).when(javaMailSender).send(any(SimpleMailMessage.class));

        MailSendException exception = assertThrows(MailSendException.class, () -> emailSendingService.sendEmail(email, subject, body));
        assertEquals("Email sending failed", exception.getMessage());
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));


    }
}