package api.gossip.uz.repository;

import api.gossip.uz.entity.ProfileRoleEntity;
import api.gossip.uz.enums.ProfileRole;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity, Integer> {
    @Transactional
    @Modifying
    void deleteByProfileId(Integer profileId);

    @Query("select p.roles from ProfileRoleEntity p where p.profileId = :profileId")
    List<ProfileRole> getAllRolesListByProfileId(@Param("profileId") Integer profileId);
}
