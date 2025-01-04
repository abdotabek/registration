package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.AuthDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.RegistrationDTO;
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

    @GetMapping("/registration/verification/{token}")
    public ResponseEntity<String> regVerification(@PathVariable("token") String token,
                                                  @RequestParam(value = "language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(authService.regVerification(token, language));
    }

    //login
    @PostMapping("/login")
    public ResponseEntity<ProfileDTO> login(@RequestBody AuthDTO authDTO,
                                            @RequestHeader("Accept-Language") AppLanguage language) {
        return ResponseEntity.ok(authService.login(authDTO, language));
    }

}
