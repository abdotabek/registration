package api.gossip.uz.service;

import api.gossip.uz.dto.AttachCreateDTO;
import api.gossip.uz.dto.AttachDTO;
import api.gossip.uz.dto.ProfileDTO;
import api.gossip.uz.dto.post.*;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
import api.gossip.uz.entity.PostEntity;
import api.gossip.uz.entity.ProfileEntity;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import api.gossip.uz.exception.NotFoundException;
import api.gossip.uz.repository.CustomPostRepository;
import api.gossip.uz.repository.PostRepository;
import api.gossip.uz.util.SpringSecurityUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private AttachService attachService;
    @Mock
    private CustomPostRepository customPostRepository;
    @Mock
    private ResourceBundleService bundleService;
    private static final String POST_ID = "post-test";
    private static final String NEW_PHOTO_ID = "photo456";
    private static final String OLD_PHOTO_ID = "oldPhoto";
    private static final String PHOTO_ID = "photo-test";
    private static final Integer PROFILE_ID = 1;
    private static final int page = 0;
    private static final int size = 5;
    private static final long totalCount = 1L;

    @AfterEach
    void cleanUp() {
        postRepository.deleteAll();
    }

    @Test
    void create() {
        AttachCreateDTO attachCreateDTO = new AttachCreateDTO();
        attachCreateDTO.setId(PHOTO_ID);

        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setContent("test content");
        postCreatedDTO.setTitle("test-title");
        postCreatedDTO.setPhoto(attachCreateDTO);

        PostEntity postEntity = new PostEntity();
        postEntity.setContent(postCreatedDTO.getContent());
        postEntity.setTitle(postCreatedDTO.getTitle());
        postEntity.setPhotoId(postCreatedDTO.getPhoto().getId());
        postEntity.setVisible(true);
        postEntity.setCreatedDate(LocalDateTime.now());
        postEntity.setProfileId(PROFILE_ID);
        postEntity.setStatus(GeneralStatus.NOT_ACTIVE);

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);

        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);
        when((attachService.attachDTO(PHOTO_ID))).thenReturn(attachDTO);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            PostDTO response = postService.create(postCreatedDTO);
            assertNotNull(response);
            assertEquals(postCreatedDTO.getTitle(), response.getTitle());
            assertEquals(postCreatedDTO.getContent(), response.getContent());
            assertEquals(postCreatedDTO.getPhoto().getId(), response.getPhoto().getId());
        }
    }

    @Test
    void getProfilePostList() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setContent("test content");
        postDTO.setTitle("title test");
        postDTO.setCreatedDate(LocalDateTime.now());
        postDTO.setPhoto(null);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setContent(postDTO.getContent());
        postEntity.setTitle(postDTO.getTitle());
        postEntity.setCreatedDate(postDTO.getCreatedDate());
        postEntity.setPhoto(null);


        PageRequest pageRequest = PageRequest.of(page, size);
        List<PostEntity> postEntityList = Collections.singletonList(postEntity);
        Page<PostEntity> postEntityPage = new PageImpl<>(postEntityList, pageRequest, 1);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);

            when(postRepository.getAllByProfileIdAndVisibleTrue(PROFILE_ID, pageRequest)).thenReturn(postEntityPage);

            when(attachService.attachDTO(null)).thenReturn(null);
            Page<PostDTO> result = postService.getProfilePostList(page, size);
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertNull(result.getContent().get(0).getPhoto());
        }
    }

    @Test
    void getByIdSuccessfully() {

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);

        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setId(PROFILE_ID);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setContent("content");
        postEntity.setTitle("title");
        postEntity.setCreatedDate(LocalDateTime.now());
        postEntity.setProfile(profileEntity);
        postEntity.setPhotoId(PHOTO_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);

        PostDTO result = postService.getById(POST_ID);
        assertNotNull(result);
        assertEquals("photo-test", result.getPhoto().getId());
        assertEquals("content", result.getContent());
        assertEquals("title", result.getTitle());

        verify(postRepository, times(1)).findById(POST_ID);
        verify(attachService, times(1)).attachDTO(PHOTO_ID);
        verifyNoMoreInteractions(postRepository, attachService);
        verifyNoInteractions(bundleService);
    }

    @Test
    void getByIdThrowNotFoundException() {

        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("post.with.id.does.not.exist")).thenReturn("Post with id does not exist!");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> postService.getById(POST_ID));
        assertEquals("Post with id does not exist!", exception.getMessage());

        verify(postRepository, times(1)).findById(POST_ID);
    }

    @Test
    void update_successfullyUpdateAsOwner() {

        AttachCreateDTO attachCreateDTO = new AttachCreateDTO();
        attachCreateDTO.setId(NEW_PHOTO_ID);

        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setTitle("title");
        postCreatedDTO.setContent("content");
        postCreatedDTO.setPhoto(attachCreateDTO);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("old title");
        postEntity.setContent("old content");
        postEntity.setPhotoId(OLD_PHOTO_ID);
        postEntity.setProfileId(PROFILE_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(attachService.delete(OLD_PHOTO_ID)).thenReturn(true);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(false);

            postService.update(POST_ID, postCreatedDTO);

            verify(postRepository, times(1)).findById(POST_ID);
            verify(postRepository, times(1)).updatePost(eq(POST_ID), eq("title"), eq("content"), eq(NEW_PHOTO_ID));
            verify(attachService, times(1)).delete(OLD_PHOTO_ID);
        }
    }

    @Test
    void update_failsWhenNotOwnerAndNotAdmin() {
        AttachCreateDTO attachCreateDTO = new AttachCreateDTO();
        attachCreateDTO.setId(PHOTO_ID);

        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setTitle("title");
        postCreatedDTO.setContent("content");
        postCreatedDTO.setPhoto(attachCreateDTO);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setPhotoId(OLD_PHOTO_ID);
        postEntity.setProfileId(PROFILE_ID + 1);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(bundleService.getMessage("not.have.permission")).thenReturn("You do not have permission to update this post");

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                postService.update(POST_ID, postCreatedDTO);
            });

            assertEquals("You do not have permission to update this post", exception.getMessage());

            verify(bundleService, times(1)).getMessage("not.have.permission");
        }
    }

    @Test
    void update_successfullyAsAdmin() {
        AttachCreateDTO attachCreateDTO = new AttachCreateDTO();
        attachCreateDTO.setId(NEW_PHOTO_ID);

        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setTitle("title");
        postCreatedDTO.setContent("content");
        postCreatedDTO.setPhoto(attachCreateDTO);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("old title");
        postEntity.setContent("old content");
        postEntity.setPhotoId(OLD_PHOTO_ID);
        postEntity.setProfileId(PROFILE_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(attachService.delete(OLD_PHOTO_ID)).thenReturn(true);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(true);

            postService.update(POST_ID, postCreatedDTO);

            verify(postRepository, times(1)).findById(POST_ID);
            verify(postRepository, times(1)).updatePost(eq(POST_ID), eq("title"), eq("content"), eq(NEW_PHOTO_ID));
        }
    }


    @Test
    void changeStatus() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setStatus(GeneralStatus.NOT_ACTIVE);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(postRepository.save(any(PostEntity.class))).thenReturn(postEntity);

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(true);

            postService.changeStatus(POST_ID, postCreatedDTO);
            assertEquals(GeneralStatus.ACTIVE, postEntity.getStatus());
            verify(postRepository).findById(POST_ID);
            verify(postRepository).save(postEntity);
            verify(bundleService, never()).getMessage(anyString());
        }
    }

    @Test
    void changeStatus_shouldThrowPostNotFound() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("post.with.id.does.not.exist")).thenReturn("Post with id does not exist!");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> postService.changeStatus(POST_ID, postCreatedDTO));
        assertEquals("Post with id does not exist!", exception.getMessage());
        verify(postRepository).findById(POST_ID);
        verify(postRepository, never()).save(any());
    }

    @Test
    void changeStatus_shouldThrowNotAdmin() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setStatus(GeneralStatus.NOT_ACTIVE);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));
        when(bundleService.getMessage("not.have.permission")).thenReturn("You do not have permission to update this post");

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(false);

            RuntimeException exception = assertThrows(RuntimeException.class, () -> postService.changeStatus(POST_ID, postCreatedDTO));
            assertEquals("You do not have permission to update this post", exception.getMessage());
            verify(postRepository, never()).save(any());
        }
    }

    @Test
    void deleteById_adminSuccess() {
        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setProfileId(PROFILE_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.ADMIN)).thenReturn(true);

            postService.deleteById(POST_ID);

            verify(postRepository).findById(POST_ID);
            verify(postRepository).delete(POST_ID);
        }
    }

    @Test
    void deleteById_ownerSuccess() {
        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setProfileId(PROFILE_ID);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(postEntity));

        try (MockedStatic<SpringSecurityUtil> securityUtil = Mockito.mockStatic(SpringSecurityUtil.class)) {
            securityUtil.when(SpringSecurityUtil::getCurrentProfileId).thenReturn(PROFILE_ID);
            securityUtil.when(() -> SpringSecurityUtil.hasRole(ProfileRole.OWNER)).thenReturn(true);

            postService.deleteById(POST_ID);

            verify(postRepository).findById(POST_ID);
            verify(postRepository).delete(POST_ID);
        }
    }

    @Test
    void deleteById_ThrowNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        when(bundleService.getMessage("not.found")).thenReturn("Not found");

        NotFoundException exception = assertThrows(NotFoundException.class, () -> postService.deleteById(POST_ID));
        assertEquals("Not found", exception.getMessage());
        verify(postRepository).findById(POST_ID);
        verify(bundleService).getMessage("not.found");
        verify(postRepository, never()).delete(anyString());
        verify(bundleService, never()).getMessage("post.delete.success");
    }

    @Test
    void filter_success() {
        PostFilterDTO postFilterDTO = new PostFilterDTO();

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("title");
        postEntity.setContent("content");
        postEntity.setCreatedDate(LocalDateTime.now());
        postEntity.setPhotoId(PHOTO_ID);

        List<PostEntity> entityList = List.of(postEntity);
        FilterResultDTO<PostEntity> filterResult = new FilterResultDTO<>(entityList, totalCount);

        when(customPostRepository.filter(postFilterDTO, page, size)).thenReturn(filterResult);

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);
        attachDTO.setUrl("http://localhost1");

        when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);
        Page<PostDTO> result = postService.filter(postFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        PostDTO postDTO = result.getContent().get(0);
        assertEquals("title", postDTO.getTitle());
        assertEquals("content", postDTO.getContent());
        assertNotNull(postDTO.getCreatedDate());
        assertNotNull(postDTO.getPhoto());
        assertEquals(PHOTO_ID, postDTO.getPhoto().getId());
        assertEquals("http://localhost1", postDTO.getPhoto().getUrl());

        verify(customPostRepository).filter(postFilterDTO, page, size);
        verify(attachService).attachDTO(PHOTO_ID);
    }

    @Test
    void filter_withNotPhoto() {

        PostFilterDTO postFilterDTO = new PostFilterDTO();

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("title");
        postEntity.setContent("content");
        postEntity.setCreatedDate(LocalDateTime.now());
        postEntity.setPhotoId(null);

        List<PostEntity> postList = List.of(postEntity);
        FilterResultDTO<PostEntity> filterResult = new FilterResultDTO<>(postList, totalCount);

        when(customPostRepository.filter(postFilterDTO, page, size)).thenReturn(filterResult);

        Page<PostDTO> result = postService.filter(postFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        PostDTO postResult = result.getContent().get(0);
        assertNotNull(postResult);
        assertEquals("post-test", postResult.getId());
        assertNull(postResult.getPhoto());

        verify(customPostRepository).filter(postFilterDTO, page, size);
        verify(attachService).attachDTO(null);
    }

    @Test
    void filter_resultEmpty() {
        PostFilterDTO postFilterDTO = new PostFilterDTO();

        FilterResultDTO<PostEntity> filterResult = new FilterResultDTO<>(List.of(), 0L);
        when(customPostRepository.filter(postFilterDTO, page, size)).thenReturn(filterResult);

        Page<PostDTO> result = postService.filter(postFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(customPostRepository).filter(postFilterDTO, page, size);
    }

    @Test
    void adminFilter_success() {
        PostAdminFilterDTO postAdminFilterDTO = new PostAdminFilterDTO();

        Object[] obj = new Object[7];
        obj[0] = POST_ID;
        obj[1] = "title";
        obj[2] = PHOTO_ID;
        obj[3] = LocalDateTime.now();
        obj[4] = PROFILE_ID;
        obj[5] = "ali";
        obj[6] = "aliyev";

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);
        attachDTO.setUrl("http://localhost1");

        List<Object[]> entityList = List.of(new Object[][]{obj});
        FilterResultDTO<Object[]> filterResult = new FilterResultDTO<>(entityList, totalCount);

        when(customPostRepository.filter(postAdminFilterDTO, page, size)).thenReturn(filterResult);
        when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);

        Page<PostDTO> result = postService.adminFilter(postAdminFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        PostDTO postDTO = result.getContent().get(0);
        assertEquals("post-test", postDTO.getId());
        assertEquals("title", postDTO.getTitle());
        assertEquals("photo-test", postDTO.getPhoto().getId());
        assertNotNull(postDTO.getCreatedDate());

        ProfileDTO profileDTO = postDTO.getProfileDTO();
        assertEquals(1, profileDTO.getId());
        assertEquals("ali", profileDTO.getName());
        assertEquals("aliyev", profileDTO.getUsername());

        verify(customPostRepository).filter(postAdminFilterDTO, page, size);
        verify(attachService).attachDTO(PHOTO_ID);
    }

    @Test
    void adminFilter_notPhoto() {
        PostAdminFilterDTO postAdminFilterDTO = new PostAdminFilterDTO();

        Object[] obj = new Object[7];
        obj[0] = POST_ID;
        obj[1] = "title";
        obj[2] = null;
        obj[3] = LocalDateTime.now();
        obj[4] = PROFILE_ID;
        obj[5] = "ali";
        obj[6] = "aliyev";

        List<Object[]> objectList = List.of(new Object[][]{obj});
        FilterResultDTO<Object[]> filterResult = new FilterResultDTO<>(objectList, totalCount);
        when(customPostRepository.filter(postAdminFilterDTO, page, size)).thenReturn(filterResult);

        Page<PostDTO> result = postService.adminFilter(postAdminFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());

        PostDTO postDTO = result.getContent().get(0);
        assertEquals("post-test", postDTO.getId());
        assertEquals("title", postDTO.getTitle());
        assertNull(postDTO.getPhoto());

        ProfileDTO profileDTO = postDTO.getProfileDTO();
        assertEquals("ali", profileDTO.getName());
        assertEquals("aliyev", profileDTO.getUsername());
    }

    @Test
    void adminFilterEmpty() {
        PostAdminFilterDTO postAdminFilterDTO = new PostAdminFilterDTO();

        List<Object[]> objectList = List.of(new Object[][]{});
        FilterResultDTO<Object[]> resultObject = new FilterResultDTO<>(objectList, 0L);
        when(customPostRepository.filter(postAdminFilterDTO, page, size)).thenReturn(resultObject);

        Page<PostDTO> result = postService.adminFilter(postAdminFilterDTO, page, size);
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        verify(customPostRepository).filter(postAdminFilterDTO, page, size);
    }


    @Test
    void getSimilarPostList_success() {
        SimilarPostListDTO similarPostListDTO = new SimilarPostListDTO();
        similarPostListDTO.setExceptId(POST_ID);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("title");
        postEntity.setContent("content");
        postEntity.setPhotoId(PHOTO_ID);
        postEntity.setVisible(true);
        postEntity.setStatus(GeneralStatus.ACTIVE);

        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(PHOTO_ID);
        attachDTO.setUrl("url");

        when(postRepository.getSimilarPostList(POST_ID)).thenReturn(List.of(postEntity));
        when(attachService.attachDTO(PHOTO_ID)).thenReturn(attachDTO);

        List<PostDTO> similarPostList = postService.getSimilarPostList(similarPostListDTO);
        assertNotNull(similarPostList);
        assertEquals(1, similarPostList.size());

        PostDTO postDTO = similarPostList.get(0);
        assertEquals("post-test", postDTO.getId());
        assertEquals("title", postDTO.getTitle());
        assertEquals("content", postDTO.getContent());
        assertNotNull(postDTO.getCreatedDate());
        assertEquals(PHOTO_ID, postDTO.getPhoto().getId());
        assertEquals("url", postDTO.getPhoto().getUrl());

        verify(postRepository, times(1)).getSimilarPostList(POST_ID);
        verify(attachService, times(1)).attachDTO(PHOTO_ID);
    }

    @Test
    void getSimilarPostList_NotPhoto() {
        SimilarPostListDTO similarPostListDTO = new SimilarPostListDTO();
        similarPostListDTO.setExceptId(POST_ID);

        PostEntity postEntity = new PostEntity();
        postEntity.setId(POST_ID);
        postEntity.setTitle("title");
        postEntity.setContent("content");
        postEntity.setPhotoId(null);
        postEntity.setStatus(GeneralStatus.ACTIVE);

        when(postRepository.getSimilarPostList(POST_ID)).thenReturn(List.of(postEntity));

        List<PostDTO> getSimilar = postService.getSimilarPostList(similarPostListDTO);
        assertNotNull(getSimilar);
        assertEquals(1, getSimilar.size());

        PostDTO result = getSimilar.get(0);
        assertEquals("post-test", result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());
        assertNull(result.getPhoto());
    }
}