package api.gossip.uz.controller;

import api.gossip.uz.dto.ProfileRoleDTO;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.service.ProfileRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileRoleControllerTest {
    @InjectMocks
    private ProfileRoleController profileRoleController;
    @Mock
    private ProfileRoleService profileRoleService;
    private static final Integer PROFILE_ROLE_ID = 1;

    @Test
    void create() {
        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(PROFILE_ROLE_ID);
        profileRoleDTO.setRoles(ProfileRole.USER);

        when(profileRoleService.create(PROFILE_ROLE_ID, ProfileRole.USER)).thenReturn(profileRoleDTO);

        ResponseEntity<ProfileRoleDTO> response = profileRoleController.create(PROFILE_ROLE_ID, ProfileRole.USER);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getId());

        verify(profileRoleService, times(1)).create(PROFILE_ROLE_ID, ProfileRole.USER);
    }

    @Test
    void get() {
        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(PROFILE_ROLE_ID);
        profileRoleDTO.setRoles(ProfileRole.USER);

        when(profileRoleService.get(PROFILE_ROLE_ID)).thenReturn(profileRoleDTO);

        ResponseEntity<ProfileRoleDTO> response = profileRoleController.get(PROFILE_ROLE_ID);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).getId());
        assertEquals(ProfileRole.USER, response.getBody().getRoles());

        verify(profileRoleService, times(1)).get(PROFILE_ROLE_ID);
    }

    @Test
    void getList() {
        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(PROFILE_ROLE_ID);
        profileRoleDTO.setRoles(ProfileRole.USER);
        profileRoleDTO.setProfileId(1);
        profileRoleDTO.setCreatedDate(LocalDateTime.now());

        List<ProfileRoleDTO> serviceResponse = new ArrayList<>(List.of(profileRoleDTO));
        when(profileRoleService.getList()).thenReturn(serviceResponse);

        ResponseEntity<List<ProfileRoleDTO>> response = profileRoleController.getList();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).get(0).getProfileId());
        assertEquals(1, response.getBody().get(0).getId());
        assertEquals(ProfileRole.USER, response.getBody().get(0).getRoles());
        assertNotNull(response.getBody().get(0).getCreatedDate());

        verify(profileRoleService, times(1)).getList();
    }

    @Test
    void update() {
        ProfileRoleDTO profileRoleDTO = new ProfileRoleDTO();
        profileRoleDTO.setId(PROFILE_ROLE_ID);
        profileRoleDTO.setRoles(ProfileRole.USER);
        profileRoleDTO.setProfileId(1);
        profileRoleDTO.setCreatedDate(LocalDateTime.now());

        when(profileRoleService.update(PROFILE_ROLE_ID, profileRoleDTO)).thenReturn(profileRoleDTO);

        ResponseEntity<ProfileRoleDTO> response = profileRoleController.update(PROFILE_ROLE_ID, profileRoleDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ProfileRoleDTO result = Objects.requireNonNull(response.getBody());
        assertEquals(1, result.getId());
        assertEquals(1, result.getProfileId());
        assertEquals(ProfileRole.USER, result.getRoles());
        assertNotNull(result.getCreatedDate());

        verify(profileRoleService, times(1)).update(PROFILE_ROLE_ID, profileRoleDTO);
    }

    @Test
    void delete() {
        doNothing().when(profileRoleService).delete(PROFILE_ROLE_ID);
        ResponseEntity<Void> response = profileRoleController.delete(PROFILE_ROLE_ID);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(profileRoleService, times(1)).delete(PROFILE_ROLE_ID);
    }
}