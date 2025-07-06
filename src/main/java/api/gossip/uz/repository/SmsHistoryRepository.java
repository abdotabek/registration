package api.gossip.uz.repository;

import api.gossip.uz.entity.SmsHistoryEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsHistoryRepository extends JpaRepository<SmsHistoryEntity, String> {
    //select count(*) from sms+history where phone =? and created_date between ? and ?
    Long countByPhoneAndCreatedDateBetween(final String phone, final LocalDateTime from, final LocalDateTime to);

    // select from SmsHistory where phone = ? order by created_date desc limit 1
    Optional<SmsHistoryEntity> findTop1ByPhoneOrderByCreatedDateDesc(final String phone);

    @Modifying
    @Transactional
    @Query("update SmsHistoryEntity set attemptCount = coalesce(attemptCount, 0) + 1 where id = :id")
    void updateAttemptCount(@Param("id") final String id);
}
