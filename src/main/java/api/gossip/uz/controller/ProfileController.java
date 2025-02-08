package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.CodeConfirmDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.ProfileDetailUpdateDTO;
import api.gossip.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.gossip.uz.dto.profile.ProfilePhotoUpdateDTO;
import api.gossip.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.service.ProfileService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileService.get(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO profileDetailUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateDetail(profileDetailUpdateDTO, language));
    }

    @PutMapping("/update-password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdateDTO profilePasswordUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updatePassword(profilePasswordUpdateDTO, language));
    }

    @PutMapping("/photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO profilePhotoUpdateDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updatePhoto(profilePhotoUpdateDTO.getPhotoId(), language));
    }

    @PutMapping("/update-username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO profileUsernameUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateUsername(profileUsernameUpdateDTO, language));
    }

    @PutMapping("/username/confirm")
    public ResponseEntity<AppResponse<String>> updateUsernameConfig(@Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateUsernameConfirm(codeConfirmDTO, language));
    }


}
