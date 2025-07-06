package api.gossip.uz.repository;

import api.gossip.uz.entity.AttachEntity;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.mapper.ProfileDetailMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProfileRepositoryTest {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    private final String PHOTO_ID = "789";
    private Integer PROFILE_ID;

    @BeforeEach
    void setUp() {
        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId(PHOTO_ID);
        attachEntity.setOriginName("originName");
        attachEntity.setVisible(true);
        testEntityManager.persistAndFlush(attachEntity);

        ProfileEntity profile = new ProfileEntity();
        profile.setName("otabek");
        profile.setUsername("abdulazizovotabek7405@hmail.com");
        profile.setVisible(true);
        profile.setStatus(GeneralStatus.ACTIVE);
        profile.setPassword("12345");
        profile.setTempUsername("eshmat");
        profile.setPhotoId(attachEntity.getId());
        testEntityManager.persistAndFlush(profile);

        PROFILE_ID = profile.getId();

    }

    @AfterEach
    void cleanUp() {
        profileRepository.deleteAll();
    }

    @Test
    void findByUsernameAndVisibleTrue() {
        Optional<ProfileEntity> result = profileRepository.findByUsernameAndVisibleTrue("abdulazizovotabek7405@hmail.com");
        assertTrue(result.isPresent());
        assertEquals("otabek", result.get().getName());
        assertEquals(true, result.get().getVisible());
    }

    @Test
    void findByIdAndVisibleTrue() {
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals(true, result.get().getVisible());
    }

    @Test
    void changeStatus() {
        // до изменении статуса
        Optional<ProfileEntity> result = profileRepository.findById(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals(GeneralStatus.ACTIVE, result.get().getStatus());

        profileRepository.changeStatus(PROFILE_ID, GeneralStatus.NOT_ACTIVE);
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменении статуса
        Optional<ProfileEntity> result1 = profileRepository.findById(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals(GeneralStatus.NOT_ACTIVE, result1.get().getStatus());
    }

    @Test
    void updatePassword() {
        // до изменении пароля
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals("12345", result.get().getPassword());

        profileRepository.updatePassword(PROFILE_ID, "54321");
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменении пароля
        Optional<ProfileEntity> result1 = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals("54321", result1.get().getPassword());
    }

    @Test
    void updateDetail() {
        // до изменения имя
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals("otabek", result.get().getName());

        profileRepository.updateDetail(PROFILE_ID, "olim");
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения имя
        Optional<ProfileEntity> result1 = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals("olim", result1.get().getName());

    }

    @Test
    void updateTempUsername() {
        // до изменения tempName
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals("eshmat", result.get().getTempUsername());

        profileRepository.updateTempUsername(PROFILE_ID, "toshmat");
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения tempName
        Optional<ProfileEntity> result1 = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals("toshmat", result1.get().getTempUsername());
    }

    @Test
    void updateUsername() {
        // до изменения username
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals("abdulazizovotabek7405@hmail.com", result.get().getUsername());

        profileRepository.updateUsername(PROFILE_ID, "sardorturdiyev3300@gmail.com");
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения username
        Optional<ProfileEntity> result1 = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals("sardorturdiyev3300@gmail.com", result1.get().getUsername());
    }

    @Test
    void updatePhoto() {
        // до изменения photoId
        Optional<ProfileEntity> result = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals(PHOTO_ID, result.get().getPhotoId());

        // из-за ограничении в базе данных создаём новый attachEntity
        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId("test12345");
        attachEntity.setOriginName("testFile");
        attachEntity.setVisible(true);
        testEntityManager.persistAndFlush(attachEntity);

        profileRepository.updatePhoto(PROFILE_ID, "test12345");
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения photoId
        Optional<ProfileEntity> result1 = profileRepository.findByIdAndVisibleTrue(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals("test12345", result1.get().getPhotoId());
    }

    @Test
    void customFilter() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<ProfileDetailMapper> result = profileRepository.customFilter(pageRequest);
        assertFalse(result.isEmpty());
        assertEquals(PROFILE_ID, result.getContent().get(0).getId());
        assertEquals("otabek", result.getContent().get(0).getName());
        assertEquals("abdulazizovotabek7405@hmail.com", result.getContent().get(0).getUsername());
        assertEquals(PHOTO_ID, result.getContent().get(0).getPhotoId());
        assertEquals(GeneralStatus.ACTIVE, result.getContent().get(0).getStatus());
    }

    @Test
    void filter() {
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<ProfileDetailMapper> result1 = profileRepository.filter("otabek", pageRequest);
        assertFalse(result1.isEmpty());
        assertEquals(PROFILE_ID, result1.getContent().get(0).getId());
        assertEquals("otabek", result1.getContent().get(0).getName());
        assertEquals("abdulazizovotabek7405@hmail.com", result1.getContent().get(0).getUsername());
        assertEquals(PHOTO_ID, result1.getContent().get(0).getPhotoId());
        assertEquals(GeneralStatus.ACTIVE, result1.getContent().get(0).getStatus());
    }

    @Test
    void delete() {
        // до изменения visible
        Optional<ProfileEntity> result = profileRepository.findById(PROFILE_ID);
        assertTrue(result.isPresent());
        assertEquals(true, result.get().getVisible());

        profileRepository.delete(PROFILE_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения visible
        Optional<ProfileEntity> result1 = profileRepository.findById(PROFILE_ID);
        assertTrue(result1.isPresent());
        assertEquals(false, result1.get().getVisible());
    }
}