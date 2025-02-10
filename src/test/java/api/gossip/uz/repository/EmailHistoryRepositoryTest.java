package api.gossip.uz.repository;

import api.gossip.uz.entity.EmailHistoryEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {EmailHistoryRepository.class})
class EmailHistoryRepositoryTest {

    EmailHistoryEntity entity;

    @BeforeEach
    void prepareData() {
        entity = new EmailHistoryEntity();
        entity.setEmail("abdulazizov7405@gmail.com");
    }

    @Test
    void countByEmailAndCreatedDateBetween() {
        assertEquals("abdulazizov7405@gmail.com", entity.getEmail());
    }

    @Test
    void findTop1ByEmailOrderByCreatedDateDesc() {
    }

    @Test
    void updateAttemptCount() {
    }
}