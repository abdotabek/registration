package api.gossip.uz.repository;

import api.gossip.uz.entity.EmailHistoryEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class EmailHistoryRepositoryTest {

    @Autowired
    EmailHistoryRepository emailHistoryRepository;
    @Autowired
    TestEntityManager testEntityManager;
    private Integer EMAIL_HISTORY_ID;

    @BeforeEach
    void setUp() {
        EmailHistoryEntity emailHistoryEntity = new EmailHistoryEntity();
        emailHistoryEntity.setEmail("test@gmail.com");
        emailHistoryEntity.setCode("test");
        emailHistoryEntity.setCreatedDate(LocalDateTime.now());
        emailHistoryEntity.setAttemptCount(0);

        testEntityManager.persistAndFlush(emailHistoryEntity);
        EMAIL_HISTORY_ID = emailHistoryEntity.getId();
        testEntityManager.clear();
    }

    @AfterEach
    void cleanUp() {
        emailHistoryRepository.deleteAll();
    }

    @Test
    void countByEmailAndCreatedDateBetween() {
        Long count = emailHistoryRepository.countByEmailAndCreatedDateBetween("test@gmail.com", LocalDateTime.now().minusMinutes(1),
                LocalDateTime.now().plusMinutes(1));
        assertEquals(1L, count);
        Optional<EmailHistoryEntity> result = emailHistoryRepository.findById(EMAIL_HISTORY_ID);
        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmail());
    }

    @Test
    void findTop1ByEmailOrderByCreatedDateDesc() {
        Optional<EmailHistoryEntity> result = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc("test@gmail.com");
        assertTrue(result.isPresent());
        assertEquals("test@gmail.com", result.get().getEmail());
        assertEquals("test", result.get().getCode());
        assertEquals(1, result.get().getId());
    }

    @Test
    void updateAttemptCount() {
        Optional<EmailHistoryEntity> count = emailHistoryRepository.findById(EMAIL_HISTORY_ID);
        assertTrue(count.isPresent());
        assertEquals(0, count.get().getAttemptCount());

        emailHistoryRepository.updateAttemptCount(EMAIL_HISTORY_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        Optional<EmailHistoryEntity> count1 = emailHistoryRepository.findById(EMAIL_HISTORY_ID);
        assertTrue(count1.isPresent());
        assertEquals(1, count1.get().getAttemptCount());

        emailHistoryRepository.updateAttemptCount(EMAIL_HISTORY_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        Optional<EmailHistoryEntity> count2 = emailHistoryRepository.findById(EMAIL_HISTORY_ID);
        assertTrue(count2.isPresent());
        assertEquals(2, count2.get().getAttemptCount());
    }
}