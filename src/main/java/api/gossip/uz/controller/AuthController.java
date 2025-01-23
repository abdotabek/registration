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
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auths")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class AuthController {

    AuthService authService;

    @PostMapping("/registration")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO registrationDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.registration(registrationDTO, language));
    }

    @GetMapping("/registration/email-verification/{token}")
    public ResponseEntity<String> emailVerification(@PathVariable("token") String token,
                                                    @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.registrationEmailVerification(token, language));
    }

    @PostMapping("/registration/sms-verification")
    public ResponseEntity<ProfileDTO> smsVerification(@Valid @RequestBody SmsVerificationDTO smsVerificationDTO,
                                                      @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.registrationSmsVerification(smsVerificationDTO, language));
    }

    @PostMapping("/registration/sms-verification-resend")
    public ResponseEntity<AppResponse<String>> smsVerificationResend(@Valid @RequestBody SmsResendDTO smsResendDTO,
                                                                     @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.registrationSmsVerificationResend(smsResendDTO, language));
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@RequestBody AuthDTO authDTO,
                                            @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok(authService.login(authDTO, language));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<AppResponse<String>> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "EN") AppLanguage language) {
        return ResponseEntity.ok(authService.resetPassword(resetPasswordDTO, language));
    }

    @PostMapping("/reset-password-confirm")
    public ResponseEntity<AppResponse<String>> resetPasswordConfirm(@RequestBody ResetPasswordConfirmDTO resetPasswordConfirmDTO,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.resetPasswordConfirm(resetPasswordConfirmDTO, language));
    }

}
