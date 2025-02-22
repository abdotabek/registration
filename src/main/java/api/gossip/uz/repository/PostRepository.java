package api.gossip.uz.repository;

import api.gossip.uz.entity.PostEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends CrudRepository<PostEntity, String> {

    List<PostEntity> getAllByProfileIdAndVisibleTrue(Integer id);

    @Modifying
    @Transactional
    @Query("update PostEntity set title =:title, content =:content, photoId =:photoId where id =:id")
    void updatePost(@Param("id") String id,
                    @Param("title") String title,
                    @Param("content") String content,
                    @Param("photoId") String photoId);
}
