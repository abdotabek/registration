package api.gossip.uz.service;

import api.gossip.uz.dto.post.FilterResultDTO;
import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.CustomRepository;
import api.gossip.uz.repository.PostRepository;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PostService {

    PostRepository postRepository;
    AttachService attachService;
    CustomRepository customRepository;

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

    public Page<PostDTO> getProfilePostList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer id = SpringSecurityUtil.getCurrentProfileId();
        Page<PostEntity> postEntities = postRepository.getAllByProfileIdAndVisibleTrue(id, pageRequest);

        List<PostDTO> list = postEntities.getContent().stream()
                .map(this::toDTO)
                .toList();
        return new PageImpl<>(list, pageRequest, postEntities.getTotalElements());
    }

    public PostDTO getById(String id) {
        return this.toDTO(postRepository.findById(id).orElseThrow(
                () -> ExceptionUtil.throwNotFoundException("post with id does not exist")));
    }

    public void update(String id, PostCreatedDTO createdDTO) {
        PostEntity postEntity = new PostEntity();
        String deletePhotoId = null;
        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ADMIN) && !postEntity.getProfileId().equals(profileId)) {
            throw new RuntimeException("you do not have permission to update this post");
        }
        if (!createdDTO.getPhoto().getId().equals(postEntity.getId())) {
            deletePhotoId = postEntity.getPhotoId();
        }
        postRepository.updatePost(id, createdDTO.getTitle(),
                createdDTO.getContent(),
                createdDTO.getPhoto().getId());
        if (deletePhotoId != null) {
            attachService.delete(deletePhotoId);
        }
    }

    public void deleteById(String id) {
        PostEntity entity = postRepository.findById(id).orElseThrow();
        Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ADMIN) && !entity.getProfileId().equals(profileId)) {
            throw new RuntimeException("you do not have permission to delete this post");
        }
        postRepository.delete(id);
    }

    public PageImpl<PostDTO> filter(PostFilterDTO filterDTO, int page, int size) {
        FilterResultDTO<PostEntity> resultDTO =
                customRepository.filter(filterDTO, page, size);
        List<PostDTO> dtoList = resultDTO.getList().stream()
                .map(this::toDTO).toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDTO.getTotalCount());
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
