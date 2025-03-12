package api.gossip.uz.repository;

import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.mapper.ProfileDetailMapper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {
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


    @Query(value = "select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate," +
            " (select count (*) from post as pt where pt.profile_id = p.id ) as postCount, " +
            "(select string_agg(pr.roles, ',') from profile_role as pr where pr.profile_id = p.id) as roles " +
            "from profile as p where p.visible = true order by p.created_date desc ", nativeQuery = true,
            countQuery = "select count(*) from profile where visible = true ")
    Page<ProfileDetailMapper> customFilter(PageRequest pageRequest);


    @Query(value = "select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate," +
            " (select count (*) from post as pt where pt.profile_id = p.id ) as postCount, " +
            "(select string_agg(pr.roles, ',') from profile_role as pr where pr.profile_id = p.id) as roles " +
            "from profile as p where (lower(p.username) like :query or lower(p.name) like :query) and p.visible = true order by p.created_date desc ", nativeQuery = true,
            countQuery = "select count(*) from profile p where (lower(p.username) like :query or lower(p.name) like :query) and visible = true ")
    Page<ProfileDetailMapper> filter(@Param("query") String query, PageRequest pageRequest);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set visible = false where id= :id")
    void delete(@Param("id") Integer id);
}
