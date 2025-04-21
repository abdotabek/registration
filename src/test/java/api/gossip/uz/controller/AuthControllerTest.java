package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.auth.AuthDTO;
import api.gossip.uz.dto.auth.RegistrationDTO;
import api.gossip.uz.dto.auth.ResetPasswordConfirmDTO;
import api.gossip.uz.dto.auth.ResetPasswordDTO;
import api.gossip.uz.dto.sms.SmsResendDTO;
import api.gossip.uz.dto.sms.SmsVerificationDTO;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {
    @InjectMocks
    private AuthController authController;
    @Mock
    private AuthService authService;

    @Test
    void registration() {
        RegistrationDTO registrationDTO = new RegistrationDTO();
        registrationDTO.setName("John Doe");
        registrationDTO.setUsername("john@example.com");

        AppResponse<String> serviceResponse = new AppResponse<>("registration_id", "Registration successful");

        when(authService.registration(any(RegistrationDTO.class), eq(AppLanguage.UZ))).thenReturn(serviceResponse);
        ResponseEntity<AppResponse<String>> response = authController.registration(registrationDTO, AppLanguage.UZ);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()));
        assertEquals("Registration successful", response.getBody().getMessage());
        assertEquals("registration_id", response.getBody().getData());
    }


    @Test
    void emailVerification() {
        String token = "jwt-test";
        String serviceResponse = "Email verification successfully";

        when(authService.registrationEmailVerification(token, AppLanguage.UZ)).thenReturn(serviceResponse);
        ResponseEntity<String> response = authController.emailVerification(token, AppLanguage.UZ);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email verification successfully", response.getBody());
    }

    @Test
    void smsVerification() {
        SmsVerificationDTO smsVerificationDTO = new SmsVerificationDTO();
        smsVerificationDTO.setCode("200");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(1);
        profileDTO.setUsername("998937877405");

        when(authService.registrationSmsVerification(smsVerificationDTO, AppLanguage.UZ)).thenReturn(profileDTO);

        ResponseEntity<ProfileDTO> response = authController.smsVerification(smsVerificationDTO, AppLanguage.UZ);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProfileDTO result = response.getBody();
        assert result != null;
        assertEquals("998937877405", result.getUsername());
        assertEquals(1, result.getId());

    }

    @Test
    void smsVerificationResend() {
        SmsResendDTO smsResendDTO = new SmsResendDTO();
        smsResendDTO.setPhone("998937877405");

        AppResponse<String> serviceResponse = new AppResponse<>("registration_id", "Registration successful");

        when(authService.registrationSmsVerificationResend(any(SmsResendDTO.class), eq(AppLanguage.UZ))).thenReturn(serviceResponse);
        ResponseEntity<AppResponse<String>> response = authController.smsVerificationResend(smsResendDTO, AppLanguage.UZ);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AppResponse<String> result = response.getBody();
        assert result != null;
        assertEquals("registration_id", result.getData());
        assertEquals("Registration successful", result.getMessage());
    }

    @Test
    void login() {
        AuthDTO authDTO = new AuthDTO();
        authDTO.setUsername("998937877405");

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(1);
        profileDTO.setUsername("998937877405");

        when(authService.login(any(AuthDTO.class), eq(AppLanguage.UZ))).thenReturn(profileDTO);
        ResponseEntity<ProfileDTO> response = authController.login(authDTO, AppLanguage.UZ);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("998937877405", Objects.requireNonNull(response.getBody()).getUsername());
        assertEquals(1, response.getBody().getId());

        verify(authService).login(authDTO, AppLanguage.UZ);
    }

    @Test
    void resetPassword() {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        resetPasswordDTO.setUsername("998937877405");

        AppResponse<String> serviceResponse = new AppResponse<>("registration_id", "Registration successful");
        when(authService.resetPassword(any(ResetPasswordDTO.class), eq(AppLanguage.UZ))).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> response = authController.resetPassword(resetPasswordDTO, AppLanguage.UZ);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        AppResponse<String> result = response.getBody();
        assert result != null;
        assertEquals("registration_id", result.getData());
        assertEquals("Registration successful", result.getMessage());

        verify(authService).resetPassword(resetPasswordDTO, AppLanguage.UZ);
    }

    @Test
    void resetPasswordConfirm() {
        ResetPasswordConfirmDTO resetPasswordConfirmDTO = new ResetPasswordConfirmDTO();
        resetPasswordConfirmDTO.setUsername("998937877405");

        AppResponse<String> serviceResponse = new AppResponse<>("reset_password", "Reset successfully");
        when(authService.resetPasswordConfirm(any(ResetPasswordConfirmDTO.class), eq(AppLanguage.UZ))).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> response = authController.resetPasswordConfirm(resetPasswordConfirmDTO, AppLanguage.UZ);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        AppResponse<String> result = response.getBody();
        assert result != null;
        assertEquals("reset_password", result.getData());
        assertEquals("Reset successfully", result.getMessage());

        verify(authService).resetPasswordConfirm(resetPasswordConfirmDTO, AppLanguage.UZ);
    }
}