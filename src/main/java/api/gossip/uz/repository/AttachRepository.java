package api.gossip.uz.repository;

import api.gossip.uz.entity.AttachEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttachRepository extends JpaRepository<AttachEntity, String> {

    @Transactional
    @Modifying
    @Query("update AttachEntity set visible = false where id =:id")
    void delete(@Param("id") String id);
}
