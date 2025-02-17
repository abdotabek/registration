package api.gossip.uz.service;

import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.repository.PostRepository;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostService {

    PostRepository postRepository;
    AttachService attachService;

    public PostDTO create(PostCreatedDTO createdDTO) {
        PostEntity entity = new PostEntity();
        entity.setTitle(createdDTO.getTitle());
        entity.setContent(createdDTO.getContent());
        entity.setPhotoId(createdDTO.getPhoto().getId());
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setProfileId(SpringSecurityUtil.getCurrentProfileId());
        postRepository.save(entity);
        return toDTO(entity);
    }

    private PostDTO toDTO(PostEntity postEntity) {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postDTO.getContent());
        postDTO.setCreatedDate(postEntity.getCreatedDate());
        postDTO.setPhoto(attachService.attachDTO(postEntity.getPhotoId()));
        return postDTO;
    }
}
