package api.gossip.uz.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {EmailHistoryRepository.class})
class EmailHistoryRepositoryTest {
    @Autowired
    EmailHistoryRepository emailHistoryRepository;

    @Test
    void countByEmailAndCreatedDateBetween() {

    }

    @Test
    void findTop1ByEmailOrderByCreatedDateDesc() {
    }

    @Test
    void updateAttemptCount() {
    }
}