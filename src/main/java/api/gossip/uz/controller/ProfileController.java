package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.CodeConfirmDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.ProfileDetailUpdateDTO;
import api.gossip.uz.dto.profile.ProfileFilterDTO;
import api.gossip.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.gossip.uz.dto.profile.ProfilePhotoUpdateDTO;
import api.gossip.uz.dto.profile.ProfileStatusDTO;
import api.gossip.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.service.ProfileService;
import api.gossip.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
@Tag(name = "ProfileController", description = "Api set with working Profile")
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    @Operation(summary = "Get profile", description = "Api used get profile")
    public ResponseEntity<ProfileDTO> get(@PathVariable("id") final Integer id) {
        return ResponseEntity.ok(profileService.get(id));
    }

    @PutMapping
    @Operation(summary = "Update profile", description = "Api used update profile")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody final ProfileDetailUpdateDTO profileDetailUpdateDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Update profile : {}name", profileDetailUpdateDTO.getName());
        return ResponseEntity.ok(profileService.updateDetail(profileDetailUpdateDTO, language));
    }

    @PutMapping("/update-password")
    @Operation(summary = "Update password", description = "Api used update password")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody final ProfilePasswordUpdateDTO profilePasswordUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updatePassword(profilePasswordUpdateDTO, language));
    }

    @PutMapping("/photo")
    @Operation(summary = "Update photo", description = "Api used update photo")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody final ProfilePhotoUpdateDTO profilePhotoUpdateDTO,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Update photo : {}photoId", profilePhotoUpdateDTO.getPhotoId());
        return ResponseEntity.ok(profileService.updatePhoto(profilePhotoUpdateDTO.getPhotoId(), language));
    }

    @PutMapping("/update-username")
    @Operation(summary = "Update username", description = "Api used username")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody final ProfileUsernameUpdateDTO profileUsernameUpdateDTO,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Update username : {}username", profileUsernameUpdateDTO.getUsername());
        return ResponseEntity.ok(profileService.updateUsername(profileUsernameUpdateDTO, language));
    }

    @PutMapping("/username/confirm")
    @Operation(summary = "Confirm username", description = "Api used confirm username")
    public ResponseEntity<AppResponse<String>> updateUsernameConfig(@Valid @RequestBody final CodeConfirmDTO codeConfirmDTO,
                                                                    @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.updateUsernameConfirm(codeConfirmDTO, language));
    }

    @PostMapping("/filter")
    @Operation(summary = "Profile filter", description = "Api used for filtering profile list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ProfileDTO>> filter(@RequestBody final ProfileFilterDTO filterDTO,
                                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                                   @RequestParam(value = "size", defaultValue = "10") int size,
                                                   @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        Page<ProfileDTO> response = profileService.filter(filterDTO, PageUtil.page(page), size, language);
        log.info("Profile filter : {}query", filterDTO.getQuery());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "Change status", description = "Api used for change status from profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppResponse<String>> changeStatus(@PathVariable("id") final Integer id,
                                                            @RequestBody final ProfileStatusDTO statusDTO,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        log.info("Change status : {}status", statusDTO.getStatus());
        return ResponseEntity.ok(profileService.changeStatus(id, statusDTO.getStatus(), language));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile", description = "Api used delete profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable("id") final Integer id,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage language) {
        return ResponseEntity.ok(profileService.delete(id, language));
    }
}
