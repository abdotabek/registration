package api.gossip.uz.repository;

import api.gossip.uz.entity.SmsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, String> {
}
