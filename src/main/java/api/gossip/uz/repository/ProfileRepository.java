package api.gossip.uz.repository;

import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set status= :status where id= :id")
    void changeStatus(@Param("id") Integer id, @Param("status") GeneralStatus status);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set password =:password where id =:id")
    void updatePassword(@Param("id") Integer id, @Param("password") String password);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set name =:name where id =:id")
    void updateDetail(@Param("id") Integer id, @Param("name") String name);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set tempUsername =:tempUsername where id =:id")
    void updateTempUsername(@Param("id") Integer id, @Param("tempUsername") String tempUsername);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set username =:username where id =:id")
    void updateUsername(@Param("id") Integer id, @Param("username") String username);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set photoId =:photoId where id =:id")
    void updatePhoto(@Param("id") Integer id, @Param("photoId") String photoId);

    Page<ProfileEntity> findAllByOrderByCreatedDateDesc(PageRequest pageRequest);

    @Query("from ProfileEntity where id ==:id or lower(username) like :id or lower(name) like :id")
    Page<ProfileEntity> filter(@Param("id") Integer id, PageRequest pageRequest);
}
