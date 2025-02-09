package api.gossip.uz.repository;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ImportAutoConfiguration(JacksonAutoConfiguration.class)
@SpringBootTest(classes = {EmailHistoryRepository.class})
class EmailHistoryRepositoryTest {
    @Mock
    EmailHistoryRepository emailHistoryRepository;

    @Test
    void countByEmailAndCreatedDateBetween() {
        assertNotNull(emailHistoryRepository.countByEmailAndCreatedDateBetween("", LocalDateTime.now(), LocalDateTime.now()));
    }

    @Test
    void findTop1ByEmailOrderByCreatedDateDesc() {
    }

    @Test
    void updateAttemptCount() {
    }
}