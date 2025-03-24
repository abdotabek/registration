package api.gossip.uz.repository;

import api.gossip.uz.entity.AttachEntity;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class PostRepositoryTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TestEntityManager testEntityManager;
    private String POST_ID;
    private Integer PROFILE_ID;
    private String ATTACH_ID;

    @BeforeEach
    void setUp() {
        ProfileEntity profile = new ProfileEntity();
        profile.setVisible(true);
        profile.setName("otabek");
        profile.setUsername("abdulazizov");
        profile.setStatus(GeneralStatus.ACTIVE);
        testEntityManager.persistAndFlush(profile);
        PROFILE_ID = profile.getId();

        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId(ATTACH_ID);
        attachEntity.setVisible(true);
        attachEntity.setOriginName("attach");
        ATTACH_ID = attachEntity.getId();

        PostEntity postEntity = new PostEntity();
        postEntity.setVisible(true);
        postEntity.setContent("test");
        postEntity.setTitle("test");
        postEntity.setStatus(GeneralStatus.ACTIVE);
        postEntity.setPhotoId(ATTACH_ID);
        postEntity.setProfile(profile);

        testEntityManager.persistAndFlush(postEntity);
        POST_ID = postEntity.getId();
        testEntityManager.clear();
    }

    @AfterEach
    void cleanUp() {
        postRepository.deleteAll();
    }

    @Test
    void getAllByProfileIdAndVisibleTrue() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<PostEntity> result = postRepository.getAllByProfileIdAndVisibleTrue(PROFILE_ID, pageRequest);
        assertEquals(0, result.getTotalElements());
        assertEquals(1, result.getSize());
    }

    @Test
    void getSimilarPostList() {
        List<PostEntity> result = postRepository.getSimilarPostList("test");
        assertEquals(true, result.get(0).getVisible());
        assertEquals(GeneralStatus.ACTIVE, result.get(0).getStatus());
        assertEquals("test", result.get(0).getTitle());
        assertEquals("test", result.get(0).getContent());
    }

    @Test
    void updatePost() {
        Optional<PostEntity> result = postRepository.findById(POST_ID);
        assertTrue(result.isPresent());
        assertEquals("test", result.get().getTitle());
        assertEquals("test", result.get().getContent());
        postRepository.updatePost(POST_ID, "updated test", "updated test", ATTACH_ID);
        testEntityManager.flush();
        testEntityManager.clear();
        Optional<PostEntity> result1 = postRepository.findById(POST_ID);
        assertTrue(result1.isPresent());
        assertEquals("updated test", result1.get().getTitle());
        assertEquals("updated test", result1.get().getContent());
    }

    @Test
    void delete() {
        Optional<PostEntity> result = postRepository.findById(POST_ID);
        assertTrue(result.isPresent());
        assertEquals(true, result.get().getVisible());
        postRepository.delete(POST_ID);

        testEntityManager.flush();
        testEntityManager.clear();

        Optional<PostEntity> result1 = postRepository.findById(POST_ID);
        assertTrue(result1.isPresent());
        assertEquals(false, result1.get().getVisible());
    }
}