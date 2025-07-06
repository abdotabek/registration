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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auths")
@RequiredArgsConstructor
@Tag(name = "auth-controller", description = "controller for authentication and authorization")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registration")
    @Operation(summary = "Profile registration", description = "Api used for registration ")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody final RegistrationDTO registrationDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Registration : {} Username{}", registrationDTO.getName(), registrationDTO.getUsername());
        return ResponseEntity.ok(authService.registration(registrationDTO, language));
    }

    @GetMapping("/registration/email-verification/{token}")
    @Operation(summary = "Email verification", description = "Api used for verification")
    public ResponseEntity<String> emailVerification(@PathVariable("token") final String token,
                                                    @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        log.info("Email verification: token {}", token);
        return ResponseEntity.ok(authService.registrationEmailVerification(token, language));
    }

    @PostMapping("/registration/sms-verification")
    @Operation(summary = "Registration verification", description = "Api used registration")
    public ResponseEntity<ProfileDTO> smsVerification(@Valid @RequestBody final SmsVerificationDTO smsVerificationDTO,
                                                      @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        log.info("Sms verification: {}", smsVerificationDTO);
        return ResponseEntity.ok(authService.registrationSmsVerification(smsVerificationDTO, language));
    }

    @PostMapping("/registration/sms-verification-resend")
    @Operation(summary = "Registration sms verification", description = "Api used sms verification")
    public ResponseEntity<AppResponse<String>> smsVerificationResend(@Valid @RequestBody final SmsResendDTO smsResendDTO,
                                                                     @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        log.info("Sms verification reset: {}", smsResendDTO.getPhone());
        return ResponseEntity.ok(authService.registrationSmsVerificationResend(smsResendDTO, language));
    }

    //login
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Api used login")
    public ResponseEntity<ProfileDTO> login(@RequestBody final AuthDTO authDTO,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage language) {
        log.info("Login user: {}", authDTO.getUsername());
        return ResponseEntity.ok(authService.login(authDTO, language));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Api used reset password")
    public ResponseEntity<AppResponse<String>> resetPassword(@RequestBody final ResetPasswordDTO resetPasswordDTO,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage language) {
        log.info("Reset password: {}", resetPasswordDTO.getUsername());
        return ResponseEntity.ok(authService.resetPassword(resetPasswordDTO, language));
    }

    @PostMapping("/reset-password-confirm")
    @Operation(summary = "Reset password confirm", description = "Api used reset confirm")
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@RequestBody final ResetPasswordConfirmDTO resetPasswordConfirmDTO,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Reset password confirm: {}", resetPasswordConfirmDTO.getUsername());
        return ResponseEntity.ok(authService.resetPasswordConfirm(resetPasswordConfirmDTO, language));
    }

}
