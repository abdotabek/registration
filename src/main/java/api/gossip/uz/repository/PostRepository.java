package api.gossip.uz.repository;

import api.gossip.uz.entity.PostEntity;
import org.springframework.data.repository.CrudRepository;

public interface PostRepository extends CrudRepository<PostEntity, String> {

}
