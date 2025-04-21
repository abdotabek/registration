package api.gossip.uz.service;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.profile.ProfileDetailUpdateDTO;
import api.gossip.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.gossip.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.NotFoundException;
import api.gossip.uz.repository.ProfileRepository;
import api.gossip.uz.repository.mapper.ProfileMapper;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProfileServiceTest {
    @Mock
    ProfileRepository profileRepository;
    @Mock
    ProfileMapper mapper;
    @Mock
    ResourceBundleService bundleService;
    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    AttachService attachService;
    @InjectMocks
    ProfileService profileService;
    private static final Integer PROFILE_ID = 1;
    private static final String PHOTO_ID = "test-123";
    private static final Integer PROFILE_ROLE_ID = 1;
    private static final AppLanguage language = AppLanguage.EN;
    private static final String successMessage = "Password updated successfully";
    private static final String oldPassword = "ali";
    private static final String newPassword = "vali";
    private static final String NEW_PHOTO_ID = "12345";


    @Test
    void get_success() {

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);
        attachDTO.setOriginName("photo");

        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setProfileId(PROFILE_ID);
        profileRoleEntity.setRoles(ProfileRole.USER);

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setName("otabek");
        profile.setUsername("998937877405");
        profile.setStatus(GeneralStatus.ACTIVE);
        profile.setCreatedDate(LocalDateTime.now());
        profile.setRoleeList(List.of(profileRoleEntity));
        profile.setPhotoId(PHOTO_ID);

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);

        ProfileDTO result = profileService.get(PROFILE_ID);
        assertNotNull(result);
        assertEquals("otabek", result.getName());
        assertEquals("998937877405", result.getUsername());
        assertEquals(ProfileRole.USER, result.getRoleList().get(0));
        assertEquals(GeneralStatus.ACTIVE, result.getStatus());
        assertNotNull(result.getCreatedDate());

        AttachDTO photo = result.getPhoto();
        assertEquals("test-123", photo.getId());
        assertEquals("photo", photo.getOriginName());

        verify(profileRepository, times(1)).findById(PROFILE_ID);
        verify(attachService, times(1)).attachDTO(PHOTO_ID);
    }

    @Test
    void get_throws_notFound() {
        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.with.id.does.not.exist")).thenReturn("Profile with id does not exist!");
        NotFoundException exception = assertThrows(NotFoundException.class, () -> profileService.get(PROFILE_ID));
        assertEquals("Profile with id does not exist!", exception.getMessage());

        verify(profileRepository, times(1)).findById(PROFILE_ID);
        verify(bundleService, times(1)).getMessage("profile.with.id.does.not.exist");
    }

    @Test
    void getVerification_success() {

        ProfileRoleEntity roleEntity = new ProfileRoleEntity();
        roleEntity.setId(PROFILE_ROLE_ID);
        roleEntity.setRoles(ProfileRole.USER);

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setName("otabek");
        profile.setUsername("998937877405");
        profile.setTempUsername("temp");
        profile.setStatus(GeneralStatus.ACTIVE);
        profile.setVisible(true);
        profile.setCreatedDate(LocalDateTime.now());
        profile.setPhotoId(PHOTO_ID);
        profile.setRoleeList(List.of(roleEntity));

        when(profileRepository.findByIdAndVisibleTrue(PROFILE_ID)).thenReturn(Optional.of(profile));

        ProfileEntity verification = profileService.getVerification(PROFILE_ID);
        assertNotNull(verification);
        assertEquals("otabek", verification.getName());
        assertEquals("998937877405", verification.getUsername());
        assertEquals("temp", verification.getTempUsername());
        assertEquals(GeneralStatus.ACTIVE, verification.getStatus());
        assertEquals(true, verification.getVisible());
        assertNotNull(verification.getCreatedDate());
        assertEquals("test-123", verification.getPhotoId());
        assertEquals(ProfileRole.USER, verification.getRoleeList().get(0).getRoles());

        verify(profileRepository, times(1)).findByIdAndVisibleTrue(PROFILE_ID);
    }

    @Test
    void getVerification_throw_notFound() {
        when(profileRepository.findByIdAndVisibleTrue(PROFILE_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.with.id.does.not.exist")).thenReturn("Profile with id does not exist!");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> profileService.getVerification(PROFILE_ID));
        assertEquals("Profile with id does not exist!", exception.getMessage());
        verify(profileRepository, times(1)).findByIdAndVisibleTrue(PROFILE_ID);
        verify(bundleService, times(1)).getMessage("profile.with.id.does.not.exist");
    }


    @Test
    void updateDetail() {

        AppLanguage appLanguage = AppLanguage.EN;

        ProfileDetailUpdateDTO profileDetailUpdateDTO = new ProfileDetailUpdateDTO();
        profileDetailUpdateDTO.setName("test");

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            when(bundleService.getMessage(anyString(), eq(appLanguage))).thenReturn("Detail updated successfully");
            doNothing().when(profileRepository).updateDetail(PROFILE_ID, "test");

            AppResponse<String> response = profileService.updateDetail(profileDetailUpdateDTO, appLanguage);
            assertNotNull(response);
            assertEquals("Detail updated successfully", response.getMessage());

            verify(bundleService, never()).getMessage(anyString());
            verify(profileRepository, times(1)).updateDetail(PROFILE_ID, "test");
        }
    }

    @Test
    void updatePassword() {

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setPassword("encodedOldPassword");

        ProfilePasswordUpdateDTO updateDTO = new ProfilePasswordUpdateDTO();
        updateDTO.setOldPassword(oldPassword);
        updateDTO.setNewPassword(newPassword);

        when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
        when(bCryptPasswordEncoder.matches(oldPassword, "encodedOldPassword")).thenReturn(true);
        when(bCryptPasswordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");
        when(bundleService.getMessage("update.password.success", language)).thenReturn(successMessage);
        doNothing().when(profileRepository).updatePassword(PROFILE_ID, "encodedNewPassword");

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            AppResponse<String> response = profileService.updatePassword(updateDTO, language);

            assertNotNull(response);
            assertEquals(successMessage, response.getMessage());
            assertNull(response.getData());

            verify(profileRepository, times(1)).findById(PROFILE_ID);
            verify(bCryptPasswordEncoder, times(1)).matches(oldPassword, "encodedOldPassword");
            verify(bCryptPasswordEncoder, times(1)).encode(newPassword);
            verify(profileRepository, times(1)).updatePassword(PROFILE_ID, "encodedNewPassword");
            verify(bundleService, times(1)).getMessage("update.password.success", language);
            verifyNoMoreInteractions(profileRepository, bCryptPasswordEncoder, bundleService);
        }
    }

    @Test
    void updateUsername() {
        String oldUsername = "998914827778";
        String newUsername = "998937877405";

        ProfileEntity entity = new ProfileEntity();
        entity.setId(PROFILE_ID);
        entity.setVisible(true);
        entity.setUsername(oldUsername);

        when(profileRepository.findByUsernameAndVisibleTrue(oldUsername)).thenReturn(Optional.of(entity));

        Optional<ProfileEntity> result = profileRepository.findByUsernameAndVisibleTrue(oldUsername);
        assertTrue(result.isPresent());
        assertEquals("998914827778", result.get().getUsername());

        ProfileUsernameUpdateDTO updateDTO = new ProfileUsernameUpdateDTO();
        updateDTO.setUsername(newUsername);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            entity.setUsername(updateDTO.getUsername());
            when(profileRepository.findByUsernameAndVisibleTrue(newUsername)).thenReturn(Optional.of(entity));
            profileRepository.updateTempUsername(PROFILE_ID, newUsername);

            Optional<ProfileEntity> updatedProfile = profileRepository.findByUsernameAndVisibleTrue(newUsername);

            assertNotNull(updatedProfile);
            assertEquals("998937877405", updatedProfile.get().getUsername());
            verify(profileRepository).findByUsernameAndVisibleTrue(newUsername);
        }
    }

    @Test
    void updatePhoto_withExistingPhoto() {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setPhotoId(PHOTO_ID);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
            when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);
            when(mapper.toEntity(any(ProfileDTO.class))).thenReturn(profile);
            when(bundleService.getMessage("change.photo.success", language)).thenReturn("Photo successfully changed");

            AppResponse<String> response = profileService.updatePhoto(NEW_PHOTO_ID, language);
            assertNotNull(response);
            assertEquals("Photo successfully changed", response.getMessage());

            verify(profileRepository).findById(PROFILE_ID);
            verify(attachService).attachDTO(PHOTO_ID);
            verify(mapper).toEntity(any(ProfileDTO.class));
            verify(bundleService).getMessage("change.photo.success", language);
        }
    }

    @Test
    void updatePhoto_withNoExistPhoto() {
        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setPhotoId(null);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
            when(mapper.toEntity(any(ProfileDTO.class))).thenReturn(profile);
            when(bundleService.getMessage("change.photo.success", language)).thenReturn("Photo successfully changed");

            AppResponse<String> response = profileService.updatePhoto(NEW_PHOTO_ID, language);
            assertNotNull(response);
            assertEquals("Photo successfully changed", response.getMessage());

            verify(profileRepository).findById(PROFILE_ID);
            verify(mapper).toEntity(any(ProfileDTO.class));
            verify(bundleService).getMessage("change.photo.success", language);
        }
    }

    @Test
    void updatePhoto_withNotFound() {
        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.empty());
            when(bundleService.getMessage("profile.with.id.does.not.exist")).thenReturn("Profile with id does not exist!");
            NotFoundException exception = assertThrows(NotFoundException.class, () -> profileService.updatePhoto(NEW_PHOTO_ID, language));
            assertNotNull(exception);
            assertEquals("Profile with id does not exist!", exception.getMessage());

            verify(profileRepository).findById(PROFILE_ID);
            verify(bundleService).getMessage("profile.with.id.does.not.exist");
        }
    }

    @Test
    void updatePhoto_unauthenticated_throwsSecurityException() {
        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(null);
            assertThrows(NotFoundException.class, () -> profileService.updatePhoto(NEW_PHOTO_ID, language));
        }
    }

    /*@Test
    void updateUsernameConfirm_success_phoneTempUsername() {
        List<ProfileRole> roleList = List.of(ProfileRole.USER);
        CodeConfirmDTO codeConfirmDTO = new CodeConfirmDTO();
        codeConfirmDTO.setCode(CODE);

        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setTempUsername(TEMP_USERNAME);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class);
             MockedStatic<PhoneUtil> phoneUtil = Mockito.mockStatic(PhoneUtil.class);
             MockedStatic<EmailUtil> emailUtil = Mockito.mockStatic(EmailUtil.class);
             MockedStatic<JwtUtil> jwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            phoneUtil.when(() -> PhoneUtil.isPhone(TEMP_USERNAME)).thenReturn(true);
            emailUtil.when(() -> EmailUtil.isEmail(TEMP_USERNAME)).thenReturn(false);
            jwtUtil.when(() -> JwtUtil.encode(TEMP_USERNAME, PROFILE_ID, roleList)).thenReturn(JWT);

            when(profileRepository.findById(PROFILE_ID)).thenReturn(Optional.of(profile));
            doNothing().when(smsHistoryService).check(TEMP_USERNAME, CODE, language);
            doNothing().when(profileRepository).updateUsername(PROFILE_ID, TEMP_USERNAME);
            when(profileRoleRepository.getAllRolesListByProfileId(PROFILE_ID)).thenReturn(roleList);
            when(bundleService.getMessage("change.username.success", language)).thenReturn("Username successfully changed");

            AppResponse<String> response = profileService.updateUsernameConfirm(codeConfirmDTO, language);
            assertNotNull(response);
            assertEquals("Username successfully changed", response.getMessage());
            assertEquals(JWT, response.getData());
        }
    }*/
}