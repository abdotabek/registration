package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.CodeConfirmDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.*;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.service.ProfileService;
import api.gossip.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Tag(name = "ProfileController", description = "Api set with working Profile")
public class ProfileController {

    ProfileService profileService;

    @GetMapping("/{id}")
    @Operation(summary = "Get profile", description = "Api used get profile")
    public ResponseEntity<ProfileDTO> get(@PathVariable("id") Integer id) {
        return ResponseEntity.ok(profileService.get(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile", description = "Api used delete profile")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        profileService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping
    @Operation(summary = "Update profile", description = "Api used update profile")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO profileDetailUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateDetail(profileDetailUpdateDTO, language));
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update password", description = "Api used update password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdateDTO profilePasswordUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updatePassword(profilePasswordUpdateDTO, language));
    }

    @PutMapping("/photo")
    @Operation(summary = "Update photo", description = "Api used update photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO profilePhotoUpdateDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updatePhoto(profilePhotoUpdateDTO.getPhotoId(), language));
    }

    @PutMapping("/update-username")
    @Operation(summary = "Update username", description = "Api used username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO profileUsernameUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateUsername(profileUsernameUpdateDTO, language));
    }

    @PutMapping("/username/confirm")
    @Operation(summary = "Confirm username", description = "Api used confirm username")
    public ResponseEntity<AppResponse<String>> updateUsernameConfig(@Valid @RequestBody CodeConfirmDTO codeConfirmDTO,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateUsernameConfirm(codeConfirmDTO, language));
    }

    @PostMapping("/filter")
    @Operation(summary = "Profile filter", description = "Api used for filtering profile list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppResponse<String>> filter(@RequestBody ProfileFilterDTO filterDTO,
                                                      @RequestParam(value = "page", defaultValue = "1") int page,
                                                      @RequestParam(value = "size", defaultValue = "10") int size,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        AppResponse<String> response = profileService.filter(filterDTO, PageUtil.page(page), size, language);
        return ResponseEntity.ok(response);
    }


}
