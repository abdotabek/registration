package api.gossip.uz.service;

import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.PostRepository;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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

    public List<PostDTO> getProfilePostList() {
        Integer id = SpringSecurityUtil.getCurrentProfileId();
        List<PostEntity> postEntities = postRepository.getAllByProfileIdAndVisibleTrue(id);
        return postEntities.stream()
                .map(this::toDTO)
                .toList();
    }

    public PostDTO getById(String id) {
        return this.toDTO(postRepository.findById(id).orElseThrow(
                () -> ExceptionUtil.throwNotFoundException("post with id does not exist")));
    }

    public void update(String id, PostCreatedDTO createdDTO) {
        postRepository.updatePost(id, createdDTO.getTitle(),
                createdDTO.getContent(),
                createdDTO.getPhoto().getId());
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
