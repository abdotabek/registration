package api.gossip.uz.repository;

import api.gossip.uz.entity.AttachEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AttachRepositoryTest {

    @Autowired
    private AttachRepository attachRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setup() {
        AttachEntity attachEntity = new AttachEntity();
        attachEntity.setId("123");
        attachEntity.setOriginName("test");
        attachEntity.setSize(10L);
        attachEntity.setExtension("test");
        attachEntity.setPath("test");
        attachEntity.setVisible(true);
        testEntityManager.persistAndFlush(attachEntity);

        attachRepository.delete("123");

        testEntityManager.flush();
        testEntityManager.clear();
    }

    @Test
    void delete() {
        Optional<AttachEntity> beforeDelete = attachRepository.findById("123");
        assertTrue(beforeDelete.isPresent());
        assertEquals("123", beforeDelete.get().getId());
        assertEquals("test", beforeDelete.get().getPath());
        assertEquals("test", beforeDelete.get().getExtension());
        assertEquals("test", beforeDelete.get().getOriginName());
        assertEquals(false, beforeDelete.get().getVisible());
        assertEquals(10, beforeDelete.get().getSize());

    }
}