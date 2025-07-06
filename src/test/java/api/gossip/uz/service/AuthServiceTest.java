package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.dto.auth.AuthDTO;
import api.gossip.uz.dto.auth.RegistrationDTO;
import api.gossip.uz.dto.auth.ResetPasswordConfirmDTO;
import api.gossip.uz.dto.auth.ResetPasswordDTO;
import api.gossip.uz.dto.sms.SmsResendDTO;
import api.gossip.uz.dto.sms.SmsVerificationDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ConflictException;
import api.gossip.uz.exception.CustomIllegalArgumentException;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.exception.NotFoundException;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.ProfileRoleRepository;
import api.gossip.uz.util.EmailUtil;
import api.gossip.uz.util.JwtUtil;
import api.gossip.uz.util.PhoneUtil;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private ProfileRepository profileRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ProfileRoleService profileRoleService;
    @Mock
    private ProfileService profileService;
    @Mock
    private ProfileRoleRepository profileRoleRepository;
    @Mock
    private ResourceBundleService bundleService;
    @Mock
    private SmsSendService smsSendService;
    @Mock
    private SmsHistoryService smsHistoryService;
    @Mock
    private EmailHistoryService emailHistoryService;
    @Mock
    private AttachService attachService;
    @Mock
    private EmailSendingService emailSendingService;
    @InjectMocks
    private AuthService authService;

    private static final Integer PROFILE_ID = 1;

    @Test
    void registrationSuccessWhenProfileNotExists() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("otabek");
        registrationDTO.setUsername("abdulazizovotabek7405@gmail.com");
        registrationDTO.setPassword("12345");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(registrationDTO.getUsername())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(profileRepository.save(any(ProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(profileRoleService.create(null, ProfileRole.USER)).thenReturn(new ProfileRoleDTO());
        doNothing().when(emailSendingService).sendRegistrationEmail(registrationDTO.getUsername(), null, language);
        when(bundleService.getMessage("email.confirm.send", language)).thenReturn("Activation link send to your email");

        AppResponse<String> response = authService.registration(registrationDTO, language);

        assertNotNull(response);
        assertEquals("Activation link send to your email", response.getMessage());
        verify(profileRoleService).create(null, ProfileRole.USER);
        verify(emailSendingService).sendRegistrationEmail(registrationDTO.getUsername(), null, language);
        verify(smsSendService, never()).sendRegistration(anyString(), any(AppLanguage.class));

    }

    @Test
    void registrationWhenProfileInRegistration() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("otabek");
        registrationDTO.setUsername("abdulazizovotabek7405@gmail.com");
        registrationDTO.setPassword("12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity existingProfile = new ProfileEntity();
        existingProfile.setId(PROFILE_ID);
        existingProfile.setStatus(GeneralStatus.IN_REGISTRATION);

        when(profileRepository.findByUsernameAndVisibleTrue(registrationDTO.getUsername())).thenReturn(Optional.of(existingProfile));
        doNothing().when(profileRoleService).deleteRoles(PROFILE_ID);
        doNothing().when(profileRepository).delete(existingProfile);
        when(bCryptPasswordEncoder.encode("12345")).thenReturn("encodedPassword");
        when(profileRepository.save(any(ProfileEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(profileRoleService.create(null, ProfileRole.USER)).thenReturn(new ProfileRoleDTO());
        doNothing().when(emailSendingService).sendRegistrationEmail(registrationDTO.getUsername(), null, language);
        when(bundleService.getMessage("email.confirm.send", language)).thenReturn("Activation link send to your email");

        AppResponse<String> response = authService.registration(registrationDTO, language);
        assertNotNull(response);
        assertEquals("Activation link send to your email", response.getMessage());
        verify(profileRoleService).deleteRoles(PROFILE_ID);
        verify(profileRepository).delete(existingProfile);
        verify(emailSendingService).sendRegistrationEmail(registrationDTO.getUsername(), null, language);
        verify(smsSendService, never()).sendRegistration(anyString(), any(AppLanguage.class));
    }

    @Test
    void registrationWhenProfileActiveThrowsExceptions() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("otabek");
        registrationDTO.setUsername("abdulazizovotabek7405@gmail.com");
        registrationDTO.setPassword("12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity existProfile = new ProfileEntity();
        existProfile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(registrationDTO.getUsername())).thenReturn(Optional.of(existProfile));
        when(bundleService.getMessage("email.phone.exist", language)).thenReturn("Activation link send to your email");

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.registration(registrationDTO, language));
        assertEquals("Activation link send to your email", exception.getMessage());
        verify(profileRoleService, never()).create(anyInt(), any(ProfileRole.class));
        verify(emailSendingService, never()).sendRegistrationEmail(anyString(), anyInt(), any(AppLanguage.class));
        verify(smsSendService, never()).sendRegistration(anyString(), any(AppLanguage.class));
    }

    @Test
    void successfulRegistrationEmailVerification() {
        String token = "valid.token.here";
        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setStatus(GeneralStatus.IN_REGISTRATION);

        try (MockedStatic<JwtUtil> mockedStatic = Mockito.mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.decodeRegVerToken(token)).thenReturn(PROFILE_ID);

            when(profileService.getVerification(PROFILE_ID)).thenReturn(profile);
            doNothing().when(profileRepository).changeStatus(PROFILE_ID, GeneralStatus.ACTIVE);
            when(bundleService.getMessage("email.ver.success", language)).thenReturn("Verification successful");

            String result = authService.registrationEmailVerification(token, language);
            assertEquals("Verification successful", result);
            verify(profileService).getVerification(PROFILE_ID);
            verify(profileRepository).changeStatus(PROFILE_ID, GeneralStatus.ACTIVE);
            verify(bundleService).getMessage("email.ver.success", language);
        }
    }

    @Test
    void invalidTokenRegistrationVerification() {
        String token = "invalid.token.here";
        AppLanguage language = AppLanguage.EN;

        try (MockedStatic<JwtUtil> mockedStatic = Mockito.mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.decodeRegVerToken(token)).thenThrow(new JwtException("invalid token"));
            when(bundleService.getMessage("reg.failed.profile.block", language)).thenReturn("Registration failed: Profile is blocked.");
        }
        ConflictException exception = assertThrows(ConflictException.class, () -> authService.registrationEmailVerification(token, language));
        assertEquals("Registration failed: Profile is blocked.", exception.getMessage());
        verify(profileRepository, never()).changeStatus(anyInt(), any());
    }

    @Test
    void notRegistrationVerification() {
        String token = "valid.token";
        AppLanguage language = AppLanguage.EN;
        ProfileEntity profile = new ProfileEntity();
        profile.setStatus(GeneralStatus.ACTIVE);

        try (MockedStatic<JwtUtil> mockedStatic = Mockito.mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.decodeRegVerToken(token)).thenReturn(PROFILE_ID);
            when(bundleService.getMessage("reg.failed.profile.block", language)).thenReturn("Registration failed: Profile is blocked.");
        }
        ConflictException exception = assertThrows(ConflictException.class, () -> authService.registrationEmailVerification(token, language));
        assertEquals("Registration failed: Profile is blocked.", exception.getMessage());
        verify(profileRepository, never()).changeStatus(anyInt(), any());
    }

    @Test
    void profileNotFoundRegistrationVerification() {
        String token = "invalid.token";
        AppLanguage language = AppLanguage.EN;

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.decodeRegVerToken(token)).thenReturn(PROFILE_ID);

            when(profileService.getVerification(PROFILE_ID)).thenAnswer(invocation -> {
                throw ExceptionUtil.throwNotFoundException("Profile not found");
            });
            NotFoundException exception = assertThrows(NotFoundException.class, () -> {
                authService.registrationEmailVerification(token, language);
            });
            assertEquals("Profile not found", exception.getMessage());
            verify(profileRepository, never()).changeStatus(anyInt(), any());
            verify(profileService).getVerification(PROFILE_ID);
        }
    }

    @Test
    void loginSuccess() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("abdulazizovotabek7405@gmail.com");
        authDTO.setPassword("password12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername("abdulazizovotabek7405@gmail.com");
        profile.setPassword("$12345@$$");
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername())).thenReturn(Optional.of(profile));
        when(bCryptPasswordEncoder.matches(authDTO.getPassword(), profile.getPassword())).thenReturn(true);

        ProfileDTO profileDTO = authService.login(authDTO, language);
        assertNotNull(profileDTO);
        verify(profileRepository).findByUsernameAndVisibleTrue(authDTO.getUsername());
        verify(bCryptPasswordEncoder).matches(authDTO.getPassword(), profile.getPassword());
    }

    @Test
    void loginUsernameNotFound() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("abdulazizovotabek7405@gmail.com");
        authDTO.setPassword("password12345");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername())).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.password.wrong", language)).thenReturn("Profile or password is wrong");

        CustomIllegalArgumentException exception = assertThrows(CustomIllegalArgumentException.class, () -> authService.login(authDTO, language));
        assertEquals("Profile or password is wrong", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(authDTO.getUsername());
        verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void loginWrongPassword() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("abdulazizovotabek7405@gmail.com");
        authDTO.setPassword("password12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername("abdulazizovotabek7405@gmail.com");
        profile.setPassword("test12345");
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername())).thenReturn(Optional.of(profile));
        when(bCryptPasswordEncoder.matches(authDTO.getPassword(), profile.getPassword())).thenReturn(false);
        when(bundleService.getMessage("profile.password.wrong", language)).thenReturn("Profile or password is wrong");

        CustomIllegalArgumentException exception = assertThrows(CustomIllegalArgumentException.class, () -> authService.login(authDTO, language));
        assertEquals("Profile or password is wrong", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(authDTO.getUsername());
        verify(bCryptPasswordEncoder).matches(authDTO.getPassword(), profile.getPassword());
    }

    @Test
    void loginInactiveStatus() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("abdulazizovotabek7405@gmail.com");
        authDTO.setPassword("test12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername("abdulazizovotabek7405@gmail.com");
        profile.setPassword("test12345");
        profile.setStatus(GeneralStatus.IN_REGISTRATION);

        when(profileRepository.findByUsernameAndVisibleTrue(authDTO.getUsername())).thenReturn(Optional.of(profile));
        when(bCryptPasswordEncoder.matches(authDTO.getPassword(), profile.getPassword())).thenReturn(true);
        when(bundleService.getMessage("profile.status", language)).thenReturn("Wrong status");

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.login(authDTO, language));
        assertEquals("Wrong status", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(authDTO.getUsername());
        verify(bCryptPasswordEncoder).matches(authDTO.getPassword(), profile.getPassword());
    }

    @Test
    void registrationSmsVerificationSuccess() {
        SmsVerificationDTO smsVerificationDTO = new SmsVerificationDTO();
        smsVerificationDTO.setPhone("998937877405");
        smsVerificationDTO.setCode("12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(smsVerificationDTO.getPhone());
        profile.setStatus(GeneralStatus.IN_REGISTRATION);
        profile.setName("testUser");

        ProfileDTO expectedProfileDTO = new ProfileDTO();
        expectedProfileDTO.setId(PROFILE_ID);
        expectedProfileDTO.setUsername(smsVerificationDTO.getPhone());
        expectedProfileDTO.setName("testUser");

        when(profileRepository.findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone())).thenReturn(Optional.of(profile));
        doNothing().when(smsHistoryService).check(smsVerificationDTO.getPhone(), smsVerificationDTO.getCode(), language);
        doNothing().when(profileRepository).changeStatus(PROFILE_ID, GeneralStatus.ACTIVE);
        when(attachService.attachDTO(null)).thenReturn(null);
        when(profileRoleRepository.getAllRolesListByProfileId(PROFILE_ID)).thenReturn(Collections.emptyList());

        try (MockedStatic<JwtUtil> mockedStatic = Mockito.mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.encode(profile.getUsername(), profile.getId(), Collections.emptyList())).thenReturn("mocked-jwt-token");

            ProfileDTO result = authService.registrationSmsVerification(smsVerificationDTO, language);
            assertNotNull(result);
            assertEquals(expectedProfileDTO.getId(), result.getId());
            assertEquals(expectedProfileDTO.getUsername(), result.getUsername());
            assertEquals(expectedProfileDTO.getName(), result.getName());
            assertEquals("mocked-jwt-token", result.getJwt());
            assertNull(result.getPhoto());

            verify(profileRepository).findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone());
            verify(smsHistoryService).check(smsVerificationDTO.getPhone(), smsVerificationDTO.getCode(), language);
            verify(profileRepository).changeStatus(PROFILE_ID, GeneralStatus.ACTIVE);
            verify(attachService).attachDTO(null);
            verify(profileRoleRepository).getAllRolesListByProfileId(PROFILE_ID);
        }
    }

    @Test
    void registrationSmsVerificationProfileNotFound() {
        SmsVerificationDTO smsVerificationDTO = new SmsVerificationDTO();
        smsVerificationDTO.setPhone("998937877405");
        smsVerificationDTO.setCode("123");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone())).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.not.found", language)).thenReturn("Profile not found");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.registrationSmsVerification(smsVerificationDTO, language));
        assertEquals("Profile not found", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone());
        verify(smsHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
        verify(profileRepository, never()).changeStatus(anyInt(), any(GeneralStatus.class));
    }

    @Test
    void registrationSmsVerificationWrongStatus() {
        SmsVerificationDTO smsVerificationDTO = new SmsVerificationDTO();
        smsVerificationDTO.setPhone("998937877405");
        smsVerificationDTO.setCode("12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setName("testName");
        profile.setUsername(smsVerificationDTO.getPhone());
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone())).thenReturn(Optional.of(profile));
        when(bundleService.getMessage("email.phone.exist", language)).thenReturn("Email or Phone already exist!");

        ConflictException exception = assertThrows(ConflictException.class, () -> authService.registrationSmsVerification(smsVerificationDTO, language));
        assertEquals("Email or Phone already exist!", exception.getMessage());
        verify(profileRepository).findByUsernameAndVisibleTrue(smsVerificationDTO.getPhone());
        verify(smsHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
        verify(profileRepository, never()).changeStatus(anyInt(), any(GeneralStatus.class));
    }

    @Test
    void registrationSmsVerificationResendSuccess() {
        SmsResendDTO smsResendDTO = new SmsResendDTO();
        smsResendDTO.setPhone("998937877405");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(smsResendDTO.getPhone());
        profile.setStatus(GeneralStatus.IN_REGISTRATION);

        when(profileRepository.findByUsernameAndVisibleTrue(smsResendDTO.getPhone())).thenReturn(Optional.of(profile));
        doNothing().when(smsSendService).sendRegistration(smsResendDTO.getPhone(), language);
        when(bundleService.getMessage("sms.resend", language)).thenReturn("Sms was sent");

        AppResponse<String> response = authService.registrationSmsVerificationResend(smsResendDTO, language);
        assertNotNull(response);
        assertEquals("Sms was sent", response.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(smsResendDTO.getPhone());
        verify(smsSendService).sendRegistration(smsResendDTO.getPhone(), language);
        verify(bundleService).getMessage("sms.resend", language);
    }

    @Test
    void registrationSmsVerificationResendProfileNotFound() {
        SmsResendDTO smsResendDTO = new SmsResendDTO();
        smsResendDTO.setPhone("998937877405");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(smsResendDTO.getPhone())).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.not.found", language)).thenReturn("Profile not found");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.registrationSmsVerificationResend(smsResendDTO, language));
        assertEquals("Profile not found", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(smsResendDTO.getPhone());
        verify(smsSendService, never()).sendRegistration(anyString(), any(AppLanguage.class));
        verify(bundleService).getMessage("profile.not.found", language);
    }

    @Test
    void registrationSmsVerificationResendWrongStatus() {
        SmsResendDTO smsResendDTO = new SmsResendDTO();
        smsResendDTO.setPhone("998937877405");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(smsResendDTO.getPhone())).thenReturn(Optional.of(profile));
        when(bundleService.getMessage("email.phone.exist", language)).thenReturn("Email or Phone already exist!");
        ConflictException exception = assertThrows(ConflictException.class, () -> authService.registrationSmsVerificationResend(smsResendDTO, language));
        assertEquals("Email or Phone already exist!", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(smsResendDTO.getPhone());
        verify(smsSendService, never()).sendRegistration(anyString(), any(AppLanguage.class));
        verify(bundleService).getMessage("email.phone.exist", language);

    }

    @Test
    void resetPasswordSuccessWithEmail() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("abdulazizovotabek7405@gmail.com");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(resetPasswordDTO.getUsername());
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername())).thenReturn(Optional.of(profile));
        doNothing().when(emailSendingService).sendResetPasswordEmail(resetPasswordDTO.getUsername(), language);
        when(bundleService.getMessage("reset.password.response", language)).thenReturn("Confirm code was sent to emil/phone");

        try (MockedStatic<PhoneUtil> phoneUtilMock = Mockito.mockStatic(PhoneUtil.class);
             MockedStatic<EmailUtil> emailUtilMock = Mockito.mockStatic(EmailUtil.class)) {
            phoneUtilMock.when(() -> PhoneUtil.isPhone(resetPasswordDTO.getUsername())).thenReturn(false);
            emailUtilMock.when(() -> EmailUtil.isEmail(resetPasswordDTO.getUsername())).thenReturn(true);

            AppResponse<String> response = authService.resetPassword(resetPasswordDTO, language);
            assertNotNull(response);
            assertEquals("Confirm code was sent to emil/phone", response.getMessage());

            verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername());
            verify(smsSendService, never()).sendResetPasswordSms(anyString(), any(AppLanguage.class));
            verify(emailSendingService).sendResetPasswordEmail(resetPasswordDTO.getUsername(), language);
            verify(bundleService).getMessage("reset.password.response", language);
        }
    }

    @Test
    void resetPasswordSuccessWithSms() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("abdulazizovotabek7405@gmail.com");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(resetPasswordDTO.getUsername());
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername())).thenReturn(Optional.of(profile));
        doNothing().when(smsSendService).sendResetPasswordSms(resetPasswordDTO.getUsername(), language);
        when(bundleService.getMessage("reset.password.response", language)).thenReturn("Confirm code was send to email/phone");

        try (MockedStatic<PhoneUtil> phoneUtilMock = Mockito.mockStatic(PhoneUtil.class);
             MockedStatic<EmailUtil> emailUtilMock = Mockito.mockStatic(EmailUtil.class)) {
            phoneUtilMock.when(() -> PhoneUtil.isPhone(resetPasswordDTO.getUsername())).thenReturn(true);
            emailUtilMock.when(() -> EmailUtil.isEmail(resetPasswordDTO.getUsername())).thenReturn(false);

            AppResponse<String> response = authService.resetPassword(resetPasswordDTO, language);
            assertNotNull(response);
            assertEquals("Confirm code was send to email/phone", response.getMessage());

            verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername());
            verify(emailSendingService, never()).sendResetPasswordEmail(resetPasswordDTO.getUsername(), language);
            verify(smsSendService).sendResetPasswordSms(resetPasswordDTO.getUsername(), language);
            verify(bundleService).getMessage("reset.password.response", language);
        }
    }

    @Test
    void resetPasswordProfileNotFound() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("998937877405");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername())).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.not.found", language)).thenReturn("Profile not found");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.resetPassword(resetPasswordDTO, language));
        assertEquals("Profile not found", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername());
        verify(smsSendService, never()).sendResetPasswordSms(anyString(), any(AppLanguage.class));
        verify(emailSendingService, never()).sendResetPasswordEmail(anyString(), any(AppLanguage.class));
        verify(bundleService).getMessage("profile.not.found", language);
    }

    @Test
    void resetPasswordWrongStatus() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("998937877405");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setStatus(GeneralStatus.IN_REGISTRATION);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername())).thenReturn(Optional.of(profile));
        when(bundleService.getMessage("profile.status", language)).thenReturn("Wrong status");
        CustomIllegalArgumentException exception = assertThrows(CustomIllegalArgumentException.class, () -> authService.resetPassword(resetPasswordDTO, language));
        assertEquals("Wrong status", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordDTO.getUsername());
        verify(smsSendService, never()).sendResetPasswordSms(anyString(), any(AppLanguage.class));
        verify(emailSendingService, never()).sendResetPasswordEmail(anyString(), any(AppLanguage.class));
        verify(bundleService).getMessage("profile.status", language);
    }

    @Test
    void resetPasswordConfirmSuccessWithEmail() {
        ResetPasswordConfirmDTO resetPasswordConfirmDTO = new ResetPasswordConfirmDTO();
        resetPasswordConfirmDTO.setUsername("998937877405");
        resetPasswordConfirmDTO.setPassword("12345");
        resetPasswordConfirmDTO.setConfigCode("123");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(resetPasswordConfirmDTO.getUsername());
        profile.setPassword(resetPasswordConfirmDTO.getPassword());
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername())).thenReturn(Optional.of(profile));
        doNothing().when(emailHistoryService).check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
        when(bundleService.getMessage("reset.password.success", language)).thenReturn("Reset password successfully finished");

        try (MockedStatic<PhoneUtil> phoneUtilMock = Mockito.mockStatic(PhoneUtil.class);
             MockedStatic<EmailUtil> emailUtilMock = Mockito.mockStatic(EmailUtil.class)) {
            phoneUtilMock.when(() -> PhoneUtil.isPhone(resetPasswordConfirmDTO.getUsername())).thenReturn(false);
            emailUtilMock.when(() -> EmailUtil.isEmail(resetPasswordConfirmDTO.getUsername())).thenReturn(true);

            AppResponse<String> response = authService.resetPasswordConfirm(resetPasswordConfirmDTO, language);
            assertEquals("Reset password successfully finished", response.getMessage());

            verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername());
            verify(smsHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
            verify(emailHistoryService).check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
            verify(bundleService).getMessage("reset.password.success", language);
        }
    }

    @Test
    void resetPasswordConfirmSuccessWithSms() {
        ResetPasswordConfirmDTO resetPasswordConfirmDTO = new ResetPasswordConfirmDTO();
        resetPasswordConfirmDTO.setUsername("998937877405");
        resetPasswordConfirmDTO.setConfigCode("123");
        resetPasswordConfirmDTO.setPassword("test12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(resetPasswordConfirmDTO.getUsername());
        profile.setStatus(GeneralStatus.ACTIVE);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername())).thenReturn(Optional.of(profile));
        doNothing().when(smsHistoryService).check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
        when(bCryptPasswordEncoder.encode(resetPasswordConfirmDTO.getPassword())).thenReturn("encodedPassword");
        doNothing().when(profileRepository).updatePassword(profile.getId(), "encodedPassword");
        when(bundleService.getMessage("reset.password.success", language)).thenReturn("Reset password successfully finished");

        try (MockedStatic<PhoneUtil> phoneUtilMock = Mockito.mockStatic(PhoneUtil.class);
             MockedStatic<EmailUtil> emailUtilMock = Mockito.mockStatic(EmailUtil.class)) {
            phoneUtilMock.when(() -> PhoneUtil.isPhone(resetPasswordConfirmDTO.getUsername())).thenReturn(true);
            emailUtilMock.when(() -> EmailUtil.isEmail(resetPasswordConfirmDTO.getUsername())).thenReturn(false);

            AppResponse<String> response = authService.resetPasswordConfirm(resetPasswordConfirmDTO, language);
            assertEquals("Reset password successfully finished", response.getMessage());

            verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername());
            verify(smsHistoryService).check(resetPasswordConfirmDTO.getUsername(), resetPasswordConfirmDTO.getConfigCode(), language);
            verify(emailHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
            verify(bCryptPasswordEncoder).encode(resetPasswordConfirmDTO.getPassword());
            verify(profileRepository).updatePassword(profile.getId(), "encodedPassword");
            verify(bundleService).getMessage("reset.password.success", language);
        }
    }

    @Test
    void resetPasswordConfirmNotFound() {
        ResetPasswordConfirmDTO resetPasswordConfirmDTO = new ResetPasswordConfirmDTO();
        resetPasswordConfirmDTO.setUsername("998937877405");
        resetPasswordConfirmDTO.setConfigCode("123");
        resetPasswordConfirmDTO.setPassword("test12345");

        AppLanguage language = AppLanguage.EN;

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername())).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.not.found", language)).thenReturn("Profile not found");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> authService.resetPasswordConfirm(resetPasswordConfirmDTO, language));
        assertEquals("Profile not found", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername());
        verify(bundleService).getMessage("profile.not.found", language);
    }

    @Test
    void resetPasswordConfirmWrongStatus() {
        ResetPasswordConfirmDTO resetPasswordConfirmDTO = new ResetPasswordConfirmDTO();
        resetPasswordConfirmDTO.setUsername("998937877405");
        resetPasswordConfirmDTO.setConfigCode("123");
        resetPasswordConfirmDTO.setPassword("test12345");

        AppLanguage language = AppLanguage.EN;

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setUsername(resetPasswordConfirmDTO.getUsername());
        profile.setStatus(GeneralStatus.IN_REGISTRATION);

        when(profileRepository.findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername())).thenReturn(Optional.of(profile));
        when(bundleService.getMessage("profile.status", language)).thenReturn("Wrong status");
        ConflictException exception = assertThrows(ConflictException.class, () -> authService.resetPasswordConfirm(resetPasswordConfirmDTO, language));
        assertEquals("Wrong status", exception.getMessage());

        verify(profileRepository).findByUsernameAndVisibleTrue(resetPasswordConfirmDTO.getUsername());
        verify(smsHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
        verify(emailHistoryService, never()).check(anyString(), anyString(), any(AppLanguage.class));
        verify(bCryptPasswordEncoder, never()).encode(anyString());
        verify(profileRepository, never()).updatePassword(anyInt(), anyString());
        verify(bundleService).getMessage("profile.status", language);
    }

    @Test
    void getLoginInResponse() {
        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setName("otabek");
        profile.setUsername("998937877405");
        profile.setPhotoId("photo123");

        List<ProfileRole> roleList = Collections.singletonList(ProfileRole.USER);

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId("photo123");
        attachDTO.setUrl("url/to/photo");

        when(profileRoleRepository.getAllRolesListByProfileId(PROFILE_ID)).thenReturn(roleList);
        when(attachService.attachDTO("photo123")).thenReturn(attachDTO);

        try (MockedStatic<JwtUtil> jwtUtilMock = Mockito.mockStatic(JwtUtil.class)) {
            jwtUtilMock.when(() -> JwtUtil.encode(profile.getUsername(), profile.getId(), roleList)).thenReturn("mocked-jwt-token");

            ProfileDTO response = authService.getLoginInResponse(profile);
            assertNotNull(response);
            assertEquals(PROFILE_ID, response.getId());
            assertEquals("otabek", response.getName());
            assertEquals("998937877405", response.getUsername());
            assertEquals(roleList, response.getRoleList());
            assertEquals("mocked-jwt-token", response.getJwt());
            assertEquals(attachDTO, response.getPhoto());
            assertEquals("photo123", response.getPhoto().getId());
            assertEquals("url/to/photo", response.getPhoto().getUrl());

            verify(profileRoleRepository).getAllRolesListByProfileId(PROFILE_ID);
            verify(attachService).attachDTO("photo123");
        }
    }

    @Test
    void get() {
        when(bCryptPasswordEncoder.encode("12345")).thenReturn("encodedPassword123");
        String result = authService.get();

        assertNotNull(result);
        assertNotNull("encodedPassword123", result);

        verify(bCryptPasswordEncoder).encode("12345");
    }
}