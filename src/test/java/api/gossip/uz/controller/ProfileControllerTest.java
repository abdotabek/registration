package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.CodeConfirmDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.*;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.service.ProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {
    @InjectMocks
    private ProfileController profileController;
    @Mock
    private ProfileService profileService;
    private static final Integer PROFILE_ID = 1;
    private static final AppLanguage LANGUAGE = AppLanguage.UZ;
    private static final String PHOTO_ID = "photo-123";
    private static final int page = 0;
    private static final int size = 10;

    @Test
    void get() {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(PROFILE_ID);
        profileDTO.setName("otabek");

        when(profileService.get(PROFILE_ID)).thenReturn(profileDTO);
        ResponseEntity<ProfileDTO> response = profileController.get(PROFILE_ID);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ProfileDTO result = Objects.requireNonNull(response.getBody());
        assertEquals("otabek", result.getName());
        assertEquals(1, result.getId());

        verify(profileService, times(1)).get(PROFILE_ID);
    }

    @Test
    void updateDetail() {
        ProfileDetailUpdateDTO profileDetailUpdateDTO = new ProfileDetailUpdateDTO();
        profileDetailUpdateDTO.setName("otabek");

        AppResponse<String> serviceResponse = new AppResponse<>(profileDetailUpdateDTO.getName(), "Profile updated successfully");
        when(profileService.updateDetail(profileDetailUpdateDTO, LANGUAGE)).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> result = profileController.updateDetail(profileDetailUpdateDTO, LANGUAGE);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Profile updated successfully", Objects.requireNonNull(result.getBody()).getMessage());
        assertEquals("otabek", result.getBody().getData());

        verify(profileService, times(1)).updateDetail(profileDetailUpdateDTO, LANGUAGE);
    }

    @Test
    void updatePassword() {

        ProfilePasswordUpdateDTO passwordUpdateDTO = new ProfilePasswordUpdateDTO();

        AppResponse<String> serviceResponse = new AppResponse<>("Password updated successfully");
        when(profileService.updatePassword(passwordUpdateDTO, LANGUAGE)).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> result = profileController.updatePassword(passwordUpdateDTO, LANGUAGE);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Password updated successfully", Objects.requireNonNull(result.getBody()).getMessage());

        verify(profileService, times(1)).updatePassword(passwordUpdateDTO, LANGUAGE);
    }

    @Test
    void updatePhoto() {
        ProfilePhotoUpdateDTO photoUpdateDTO = new ProfilePhotoUpdateDTO();
        photoUpdateDTO.setPhotoId(PHOTO_ID);

        AppResponse<String> serviceResponse = new AppResponse<>(photoUpdateDTO.getPhotoId(), "photo successfully updated");
        when(profileService.updatePhoto(PHOTO_ID, LANGUAGE)).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> response = profileController.updatePhoto(photoUpdateDTO, LANGUAGE);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("photo successfully updated", Objects.requireNonNull(response.getBody()).getMessage());
        assertEquals("photo-123", response.getBody().getData());

        verify(profileService, times(1)).updatePhoto(PHOTO_ID, LANGUAGE);
    }

    @Test
    void updateUsername() {
        ProfileUsernameUpdateDTO updateDTO = new ProfileUsernameUpdateDTO();
        updateDTO.setUsername("998937877405");

        AppResponse<String> serviceResponse = new AppResponse<>(updateDTO.getUsername(), "Username updated successfully");
        when(profileService.updateUsername(updateDTO, LANGUAGE)).thenReturn(serviceResponse);

        ResponseEntity<AppResponse<String>> response = profileController.updateUsername(updateDTO, LANGUAGE);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("998937877405", Objects.requireNonNull(response.getBody()).getData());
        assertEquals("Username updated successfully", response.getBody().getMessage());

        verify(profileService, times(1)).updateUsername(updateDTO, LANGUAGE);
    }

    @Test
    void updateUsernameConfig() {
        CodeConfirmDTO codeConfirmDTO = new CodeConfirmDTO();
        codeConfirmDTO.setCode("code-test");

        AppResponse<String> response = new AppResponse<>(codeConfirmDTO.getCode(), "Confirm code send success");
        when(profileService.updateUsernameConfirm(codeConfirmDTO, LANGUAGE)).thenReturn(response);

        ResponseEntity<AppResponse<String>> result = profileController.updateUsernameConfig(codeConfirmDTO, LANGUAGE);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("code-test", Objects.requireNonNull(result.getBody()).getData());
        assertEquals("Confirm code send success", result.getBody().getMessage());

        verify(profileService, times(1)).updateUsernameConfirm(codeConfirmDTO, LANGUAGE);
    }

    @Test
    void filter() {
        ProfileFilterDTO profileFilterDTO = new ProfileFilterDTO();
        profileFilterDTO.setQuery(1);

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(PROFILE_ID);
        profileDTO.setName("otabek");

        PageImpl<ProfileDTO> serviceResponse = new PageImpl<>(List.of(profileDTO), PageRequest.of(page, size), 1);
        when(profileService.filter(profileFilterDTO, page, size, LANGUAGE)).thenReturn(serviceResponse);
        ResponseEntity<Page<ProfileDTO>> response = profileController.filter(profileFilterDTO, 1, 10, LANGUAGE);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getContent().get(0).getId());
        assertEquals("otabek", response.getBody().getContent().get(0).getName());

        verify(profileService, times(1)).filter(profileFilterDTO, page, size, LANGUAGE);
    }

    @Test
    void changeStatus() {
        ProfileStatusDTO changeStatus = new ProfileStatusDTO();
        changeStatus.setStatus(GeneralStatus.ACTIVE);

        AppResponse<String> response = new AppResponse<>("status updated success");
        when(profileService.changeStatus(PROFILE_ID, GeneralStatus.ACTIVE, LANGUAGE)).thenReturn(response);
        ResponseEntity<AppResponse<String>> result = profileController.changeStatus(PROFILE_ID, changeStatus, LANGUAGE);
        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("status updated success", Objects.requireNonNull(result.getBody()).getMessage());

        verify(profileService, times(1)).changeStatus(PROFILE_ID, GeneralStatus.ACTIVE, LANGUAGE);
    }

    @Test
    void delete() {
        AppResponse<String> serviceResponse = new AppResponse<>("Delete success");
        when(profileService.delete(PROFILE_ID, LANGUAGE)).thenReturn(serviceResponse);
        ResponseEntity<AppResponse<String>> response = profileController.delete(PROFILE_ID, LANGUAGE);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Delete success", Objects.requireNonNull(response.getBody()).getMessage());

        verify(profileService, times(1)).delete(PROFILE_ID, LANGUAGE);
    }
}