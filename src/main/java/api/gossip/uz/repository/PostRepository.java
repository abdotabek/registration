package api.gossip.uz.repository;

import api.gossip.uz.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends CrudRepository<PostEntity, String>, PagingAndSortingRepository<PostEntity, String> {

    Page<PostEntity> getAllByProfileIdAndVisibleTrue(Integer id, Pageable pageRequest);

    @Query("from PostEntity where id !=?1 and visible = true order by createdDate desc limit 3")
    List<PostEntity> getSimilarPostList(String exceptId);

    @Query("from PostEntity where id !=:id and visible = true order by createdDate desc ")
    List<PostEntity> similar(@Param("id") String id);

    @Modifying
    @Transactional
    @Query("update PostEntity set title =:title, content =:content, photoId =:photoId where id =:id")
    void updatePost(@Param("id") String id,
                    @Param("title") String title,
                    @Param("content") String content,
                    @Param("photoId") String photoId);

    @Modifying
    @Transactional
    @Query("update PostEntity set visible = false where id = :id")
    void delete(String id);
}
