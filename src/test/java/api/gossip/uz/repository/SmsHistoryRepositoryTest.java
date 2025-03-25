package api.gossip.uz.repository;

import api.gossip.uz.entity.SmsHistoryEntity;
import api.gossip.uz.enums.SmsType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SmsHistoryRepositoryTest {
    @Autowired
    private SmsHistoryRepository repository;
    @Autowired
    private TestEntityManager testEntityManager;
    private String SMS_HISTORY_ID;

    @BeforeEach
    void setUp() {
        LocalDateTime createDate = LocalDateTime.now().minusMinutes(3);

        SmsHistoryEntity smsHistoryEntity = new SmsHistoryEntity();
        smsHistoryEntity.setId(SMS_HISTORY_ID);
        smsHistoryEntity.setSmsType(SmsType.REGISTRATION);
        smsHistoryEntity.setPhone("998937877405");
        smsHistoryEntity.setMessage("message");
        smsHistoryEntity.setAttemptCount(2);
        smsHistoryEntity.setCreatedDate(createDate);
        testEntityManager.persistAndFlush(smsHistoryEntity);
        SMS_HISTORY_ID = smsHistoryEntity.getId();

    }

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @Test
    void countByPhoneAndCreatedDateBetween() {
        LocalDateTime startDate = LocalDateTime.now().minusMinutes(5);
        LocalDateTime endDate = LocalDateTime.now();

        Long result = repository.countByPhoneAndCreatedDateBetween("998937877405", startDate, endDate);
        assertEquals(1, result);
    }

    @Test
    void findTop1ByPhoneOrderByCreatedDateDesc() {
        Optional<SmsHistoryEntity> result = repository.findTop1ByPhoneOrderByCreatedDateDesc("998937877405");
        assertTrue(result.isPresent());
        assertEquals("998937877405", result.get().getPhone());
        assertEquals(2, result.get().getAttemptCount());
    }

    @Test
    void updateAttemptCount() {
        // до добавления attempt count
        Optional<SmsHistoryEntity> result = repository.findById(SMS_HISTORY_ID);
        assertTrue(result.isPresent());
        assertEquals(2, result.get().getAttemptCount());

        repository.updateAttemptCount(SMS_HISTORY_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        // после добавления attempt count
        Optional<SmsHistoryEntity> result1 = repository.findById(SMS_HISTORY_ID);
        assertTrue(result1.isPresent());
        assertEquals(3, result1.get().getAttemptCount());

        repository.updateAttemptCount(SMS_HISTORY_ID);
        testEntityManager.flush();
        testEntityManager.clear();

        // после изменения attempt count
        Optional<SmsHistoryEntity> result2 = repository.findById(SMS_HISTORY_ID);
        assertTrue(result2.isPresent());
        assertEquals(4, result2.get().getAttemptCount());

    }
}