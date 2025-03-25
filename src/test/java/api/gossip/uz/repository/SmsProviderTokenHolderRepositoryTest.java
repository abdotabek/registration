package api.gossip.uz.repository;

import api.gossip.uz.entity.SmsProviderTokenHolderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class SmsProviderTokenHolderRepositoryTest {
    @Autowired
    private SmsProviderTokenHolderRepository repository;
    @Autowired
    private TestEntityManager testEntityManager;
    private Integer TOKEN_ID;

    @Test
    void findTop1By() {
        LocalDateTime createdDate = LocalDateTime.now();
        LocalDateTime expiredDate = LocalDateTime.now().plusMonths(1);
        SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
        entity.setId(TOKEN_ID);
        entity.setToken("testToken");
        entity.setCreatedDate(createdDate);
        entity.setExpiredDate(expiredDate);
        testEntityManager.persistAndFlush(entity);
        TOKEN_ID = entity.getId();

        Optional<SmsProviderTokenHolderEntity> result = repository.findTop1By();
        assertTrue(result.isPresent());
        assertEquals("testToken", result.get().getToken());

    }
}