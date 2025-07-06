package api.gossip.uz.service;

import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.post.FilterResultDTO;
import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.dto.post.SimilarPostListDTO;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.CustomPostRepository;
import api.gossip.uz.repository.PostRepository;
import api.gossip.uz.util.SpringSecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AttachService attachService;
    private final CustomPostRepository customPostRepository;
    private final ResourceBundleService bundleService;

    public PostDTO create(final PostCreatedDTO createdDTO) {
        final PostEntity entity = new PostEntity();
        entity.setTitle(createdDTO.getTitle());
        entity.setContent(createdDTO.getContent());
        entity.setPhotoId(createdDTO.getPhoto().getId());
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setProfileId(SpringSecurityUtil.getCurrentProfileId());
        entity.setStatus(GeneralStatus.NOT_ACTIVE);
        postRepository.save(entity);
        return toDTO(entity);
    }

    public Page<PostDTO> getProfilePostList(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Integer id = SpringSecurityUtil.getCurrentProfileId();
        Page<PostEntity> postEntities = postRepository.getAllByProfileIdAndVisibleTrue(id, pageRequest);

        List<PostDTO> list = postEntities.getContent().stream()
            .map(this::toDTO)
            .toList();
        return new PageImpl<>(list, pageRequest, postEntities.getTotalElements());
    }

    public PostDTO getById(final String id) {
        return this.toDTO(postRepository.findById(id).orElseThrow(
            () -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("post.with.id.does.not.exist"))));
    }

    public void update(final String id, final PostCreatedDTO createdDTO) {
        final PostEntity postEntity = postRepository.findById(id).orElseThrow(
            () -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("not.found")));
        String deletePhotoId = null;
        final Integer profileId = SpringSecurityUtil.getCurrentProfileId();

        if (!SpringSecurityUtil.hasRole(ProfileRole.ADMIN) && !postEntity.getProfileId().equals(profileId)) {
            throw new RuntimeException(bundleService.getMessage("not.have.permission"));
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

    public void changeStatus(final String id, final PostCreatedDTO createdDTO) {
        final PostEntity postEntity = postRepository.findById(id).orElseThrow(
            () -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("post.with.id.does.not.exist")));
        if (!SpringSecurityUtil.hasRole(ProfileRole.ADMIN)) {
            throw new RuntimeException(bundleService.getMessage("not.have.permission"));
        }
        if (GeneralStatus.NOT_ACTIVE == postEntity.getStatus()) {
            postEntity.setStatus(createdDTO.getStatus());
        }
        postRepository.save(postEntity);
    }

    public void deleteById(final String id) {
        final PostEntity entity = postRepository.findById(id).orElseThrow(
            () -> ExceptionUtil.throwNotFoundException(bundleService.getMessage("not.found")));
        final Integer profileId = SpringSecurityUtil.getCurrentProfileId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ADMIN) && !entity.getProfileId().equals(profileId)) {
            throw new RuntimeException(bundleService.getMessage("not.have.permission"));
        }
        postRepository.delete(id);
        bundleService.getMessage("post.delete.success");
    }

    public PageImpl<PostDTO> filter(final PostFilterDTO filterDTO, int page, int size) {
        final FilterResultDTO<PostEntity> resultDTO =
            customPostRepository.filter(filterDTO, page, size);
        List<PostDTO> dtoList = resultDTO.getList().stream()
            .map(this::toDTO).toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDTO.getTotalCount());
    }

    public Page<PostDTO> adminFilter(final PostAdminFilterDTO filterDTO, int page, int size) {
        FilterResultDTO<Object[]> resultDTO =
            customPostRepository.filter(filterDTO, page, size);
        List<PostDTO> dtoList = resultDTO.getList().stream()
            .map(this::toDTO).toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDTO.getTotalCount());
    }

    public List<PostDTO> getSimilarPostList(final SimilarPostListDTO similarPostListDTO) {
        List<PostEntity> postList = postRepository.getSimilarPostList(similarPostListDTO.getExceptId());
        return postList.stream().toList().stream().map(this::toDTO).toList();
    }

    protected PostDTO toDTO(final PostEntity postEntity) {
        final PostDTO postDTO = new PostDTO();
        postDTO.setId(postEntity.getId());
        postDTO.setTitle(postEntity.getTitle());
        postDTO.setContent(postEntity.getContent());
        postDTO.setCreatedDate(postEntity.getCreatedDate());
        postDTO.setPhoto(attachService.attachDTO(postEntity.getPhotoId()));
        return postDTO;
    }

    private PostDTO toDTO(final Object[] obj) {
        final PostDTO postDTO = new PostDTO();
        postDTO.setId((String) obj[0]);
        postDTO.setTitle((String) obj[1]);
        if (obj[2] != null) {
            postDTO.setPhoto(attachService.attachDTO((String) obj[2]));
        }
        postDTO.setCreatedDate((LocalDateTime) obj[3]);

        final ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId((Integer) obj[4]);
        profileDTO.setName((String) obj[5]);
        profileDTO.setUsername((String) obj[6]);
        postDTO.setProfileDTO(profileDTO);
        return postDTO;
    }

}
