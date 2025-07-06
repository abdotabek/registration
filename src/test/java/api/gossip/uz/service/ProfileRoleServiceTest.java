package api.gossip.uz.service;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.NotFoundException;
import api.gossip.uz.repository.ProfileRoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileRoleServiceTest {
    @InjectMocks
    private ProfileRoleService profileRoleService;
    @Mock
    private ProfileRoleRepository profileRoleRepository;
    @Mock
    private ResourceBundleService bundleService;
    private static final Integer PROFILE_ID = 1;
    private static final Integer PROFILE_ROLE_ID = 1;

    @Test
    void create() {

        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setId(PROFILE_ROLE_ID);
        profileRoleEntity.setProfileId(PROFILE_ID);
        profileRoleEntity.setRoles(ProfileRole.USER);

        when(profileRoleRepository.save(any(ProfileRoleEntity.class))).thenReturn(profileRoleEntity);

        ProfileRoleDTO profileRole = profileRoleService.create(PROFILE_ID, ProfileRole.USER);
        assertNotNull(profileRole);
        assertEquals(1, profileRole.getProfileId());
        assertEquals(ProfileRole.USER, profileRole.getRoles());
        verify(profileRoleRepository, times(1)).save(any(ProfileRoleEntity.class));
    }

    @Test
    void create_notFound() {

        when(bundleService.getMessage("not.found")).thenReturn("Not found");
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> profileRoleService.create(null, ProfileRole.USER));
        assertEquals("Not found", exception.getMessage());

        verify(profileRoleRepository, never()).save(any(ProfileRoleEntity.class));
    }

    @Test
    void get() {

        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setId(PROFILE_ROLE_ID);
        profileRoleEntity.setProfileId(PROFILE_ID);
        profileRoleEntity.setRoles(ProfileRole.USER);

        when(profileRoleRepository.findById(PROFILE_ROLE_ID)).thenReturn(Optional.of(profileRoleEntity));

        ProfileRoleDTO profileRole = profileRoleService.get(PROFILE_ROLE_ID);
        assertNotNull(profileRole);
        assertEquals(1, profileRole.getProfileId());
        assertEquals(1, profileRole.getId());
        assertEquals(ProfileRole.USER, profileRole.getRoles());

        verify(profileRoleRepository, times(1)).findById(PROFILE_ROLE_ID);
    }

    @Test
    void get_notFound() {
        when(profileRoleRepository.findById(PROFILE_ROLE_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("profile.role.with.id.does.not.exist")).thenReturn("Profile Role with id does not exist!");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> profileRoleService.get(PROFILE_ROLE_ID));
        assertEquals("Profile Role with id does not exist!", exception.getMessage());

        verify(profileRoleRepository, times(1)).findById(PROFILE_ROLE_ID);
    }

    @Test
    void getList() {
        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setId(PROFILE_ROLE_ID);
        profileRoleEntity.setProfileId(PROFILE_ID);
        profileRoleEntity.setRoles(ProfileRole.USER);
        profileRoleEntity.setCreatedDate(LocalDateTime.now());

        when(profileRoleRepository.findAll()).thenReturn(List.of(profileRoleEntity));

        List<ProfileRoleDTO> profileRoleDTO = profileRoleService.getList();
        assertNotNull(profileRoleDTO);
        ProfileRoleDTO result = profileRoleDTO.get(0);
        assertEquals(1, result.getProfileId());
        assertEquals(1, result.getId());
        assertEquals(ProfileRole.USER, result.getRoles());
        assertNotNull(result.getCreatedDate());

        verify(profileRoleRepository, times(1)).findAll();
    }

    @Test
    void update() {

        ProfileRoleEntity profileRoleEntity = new ProfileRoleEntity();
        profileRoleEntity.setId(PROFILE_ROLE_ID);
        profileRoleEntity.setProfileId(PROFILE_ID);
        profileRoleEntity.setRoles(ProfileRole.USER);

        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(PROFILE_ROLE_ID);
        profileRoleDTO.setProfileId(PROFILE_ID);
        profileRoleDTO.setRoles(ProfileRole.OWNER);

        when(profileRoleRepository.findById(PROFILE_ROLE_ID)).thenReturn(Optional.of(profileRoleEntity));
        when(profileRoleRepository.save(any(ProfileRoleEntity.class))).thenReturn(profileRoleEntity);

        ProfileRoleDTO result = profileRoleService.update(PROFILE_ROLE_ID, profileRoleDTO);
        assertNotNull(result);
        assertEquals(1, result.getProfileId());
        assertEquals(1, result.getId());
        assertEquals(ProfileRole.OWNER, result.getRoles());
    }


    @Test
    void delete_success() {
        profileRoleService.delete(PROFILE_ROLE_ID);
        verify(profileRoleRepository).deleteById(PROFILE_ROLE_ID);
    }

    @Test
    void deleteRoles() {
        profileRoleService.deleteRoles(PROFILE_ID);
        verify(profileRoleRepository).deleteByProfileId(PROFILE_ROLE_ID);
    }
}