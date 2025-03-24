package api.gossip.uz.repository;

import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProfileRoleRepositoryTest {
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    private Integer ROLE_ID;
    private Integer PROFILE_ID;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = new ProfileEntity();
        profile.setId(PROFILE_ID);
        profile.setVisible(true);
        profile.setStatus(GeneralStatus.ACTIVE);
        profile.setPassword("12345");
        profile.setName("eshmat");
        profile.setUsername("eshmat74@gmail.com");
        testEntityManager.persistAndFlush(profile);

        ProfileRoleEntity entity = new ProfileRoleEntity();
        entity.setId(ROLE_ID);
        entity.setProfileId(profile.getId());
        entity.setProfile(profile);
        entity.setRoles(ProfileRole.ADMIN);

        profile.setRoleeList(List.of(entity));
        testEntityManager.persistAndFlush(entity);

        PROFILE_ID = profile.getId();
        ROLE_ID = entity.getId();
    }

    @AfterEach
    void cleanUp() {
        profileRoleRepository.deleteAll();
    }

    @Test
    void deleteByProfileId() {
        // до удаления role
        Optional<ProfileRoleEntity> result = profileRoleRepository.findById(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals(ProfileRole.ADMIN, result.get().getRoles());

        profileRoleRepository.deleteByProfileId(PROFILE_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        // после удаления role
        Optional<ProfileRoleEntity> result1 = profileRoleRepository.findById(PROFILE_ID);
        assertTrue(result1.isEmpty());
    }

    @Test
    void getAllRolesListByProfileId() {
        List<ProfileRole> roleList = profileRoleRepository.getAllRolesListByProfileId(PROFILE_ID);
        assertEquals(ProfileRole.ADMIN, roleList.get(0));
    }
}