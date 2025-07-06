package api.gossip.uz.service;

import api.gossip.uz.entity.EmailHistoryEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.exception.BadRequestException;
import api.gossip.uz.repository.EmailHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EmailHistoryServiceTest {
    @Mock
    private EmailHistoryRepository emailHistoryRepository;
    @Mock
    private ResourceBundleService bundleService;
    @InjectMocks
    private EmailHistoryService emailHistoryService;
    private Integer EMAIL_HISTORY_ID = 1;

    @Test
    void create() {
        String email = "abdulazizovotabek7405@gmail.com";
        String code = "112";
        SmsType smsType = SmsType.REGISTRATION;


        when(emailHistoryRepository.save(any(EmailHistoryEntity.class))).thenAnswer(invocation -> {
            EmailHistoryEntity entity = invocation.getArgument(0);
            entity.setId(EMAIL_HISTORY_ID);
            return entity;
        });
        emailHistoryService.create(email, code, smsType);

        ArgumentCaptor<EmailHistoryEntity> argumentCaptor = ArgumentCaptor.forClass(EmailHistoryEntity.class);
        verify(emailHistoryRepository).save(argumentCaptor.capture());

        EmailHistoryEntity entity = argumentCaptor.getValue();
        assertNotNull(entity);
        assertEquals("abdulazizovotabek7405@gmail.com", entity.getEmail());
        assertEquals(SmsType.REGISTRATION, entity.getEmailType());
        assertEquals("112", entity.getCode());
    }

    @Test
    void getEmailCount() {
        String email = "abdulazizovotabek7405@gmail.com";
        Long expectedCount = 5L;

        when(emailHistoryRepository.countByEmailAndCreatedDateBetween(eq(email), any(LocalDateTime.class), any(LocalDateTime.class))).thenReturn(expectedCount);

        Long result = emailHistoryService.getEmailCount(email);
        assertEquals(expectedCount, result);
        ArgumentCaptor<LocalDateTime> startTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> endTimeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(emailHistoryRepository).countByEmailAndCreatedDateBetween(eq(email), startTimeCaptor.capture(), endTimeCaptor.capture());

        LocalDateTime startTime = startTimeCaptor.getValue();
        LocalDateTime endTime = endTimeCaptor.getValue();

        Duration duration = Duration.between(startTime, endTime);
        assertTrue(duration.toMinutes() >= 0 && duration.toMinutes() <= 1);
    }

    @Test
    void check_withValidData_passedSuccessfully() {
        String email = "abdulazizovotabek7405@gmail.com";
        String code = "123";
        AppLanguage language = AppLanguage.EN;

        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setId(EMAIL_HISTORY_ID);
        emailHistoryEntity.setEmail(email);
        emailHistoryEntity.setCode(code);
        emailHistoryEntity.setAttemptCount(0);
        emailHistoryEntity.setCreatedDate(LocalDateTime.now().minusMinutes(1));

        when(emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email)).thenReturn(Optional.of(emailHistoryEntity));
        assertDoesNotThrow(() -> emailHistoryService.check(email, code, language));
        verify(emailHistoryRepository, never()).updateAttemptCount(anyInt());
    }

    @Test
    void check_withNoHistory_throwException() {
        String email = "998937877405";
        String code = "123";
        AppLanguage language = AppLanguage.EN;

        when(emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email)).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.ver.failed", language)).thenReturn("Verification failed");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> emailHistoryService.check(email, code, language));
        assertEquals("Verification failed", exception.getMessage());
    }

    @Test
    void check_withTooManyAttempts_throws() {
        String email = "998937877405";
        String code = "123";
        AppLanguage language = AppLanguage.EN;

        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setId(EMAIL_HISTORY_ID);
        emailHistoryEntity.setEmail(email);
        emailHistoryEntity.setCode(code);
        emailHistoryEntity.setAttemptCount(3);

        when(emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email)).thenReturn(Optional.of(emailHistoryEntity));
        when(bundleService.getMessage("attempt.count", language)).thenReturn("Number of attempts exceeded");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> emailHistoryService.check(email, code, language));
        assertEquals("Number of attempts exceeded", exception.getMessage());
    }

    @Test
    void check_withWrongCode_updateAttemptAndThrows() {
        String email = "998937877405";
        String code = "123";
        AppLanguage language = AppLanguage.EN;

        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setId(EMAIL_HISTORY_ID);
        emailHistoryEntity.setEmail(email);
        emailHistoryEntity.setCode("561");
        emailHistoryEntity.setAttemptCount(0);

        when(emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email)).thenReturn(Optional.of(emailHistoryEntity));
        when(bundleService.getMessage("profile.ver.failed", language)).thenReturn("Verification failed");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> emailHistoryService.check(email, code, language));
        assertEquals("Verification failed", exception.getMessage());
        verify(emailHistoryRepository).updateAttemptCount(1);
    }

    @Test
    void check_withExpiredCode_throwsException() {
        String email = "998937877405";
        String code = "123";
        AppLanguage language = AppLanguage.EN;

        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setId(EMAIL_HISTORY_ID);
        entity.setEmail(email);
        entity.setCode(code);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now().minusMinutes(3));

        when(emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email)).thenReturn(Optional.of(entity));
        when(bundleService.getMessage("profile.ver.failed", language)).thenReturn("Verification failed");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> emailHistoryService.check(email, code, language));
        assertEquals("Verification failed", exception.getMessage());
    }
}