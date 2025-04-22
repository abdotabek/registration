package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.dto.post.SimilarPostListDTO;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.service.PostService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    private static final String POST_ID = "post-123";
    private static final String CONTENT = "content";
    private static final String TITLE = "title";
    private static final String SET_EXCEPT_ID = "ex-123";
    private static final String QUERY = "query-123";
    private static final int page = 0;
    private static final int size = 12;

    @Test
    void create() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setContent(CONTENT);
        postCreatedDTO.setTitle(TITLE);
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);

        when(postService.create(postCreatedDTO)).thenReturn(postDTO);

        ResponseEntity<PostDTO> response = postController.create(postCreatedDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        PostDTO result = response.getBody();
        assert result != null;
        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());

        verify(postService, times(1)).create(postCreatedDTO);
    }

    @Test
    void profilePostList() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);
        postDTO.setContent(CONTENT);

        Page<PostDTO> pageResult = new PageImpl<>(List.of(postDTO), PageRequest.of(page, size), 1);

        when(postService.getProfilePostList(anyInt(), eq(size))).thenReturn(pageResult);

        ResponseEntity<Page<PostDTO>> response = postController.profilePostList(page, size);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PostDTO result = Objects.requireNonNull(response.getBody()).getContent().get(0);
        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(CONTENT, result.getContent());

        verify(postService, times(1)).getProfilePostList(1, 12);
    }

    @Test
    void get() {
        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);
        postDTO.setContent(CONTENT);

        when(postService.getById(POST_ID)).thenReturn(postDTO);
        ResponseEntity<PostDTO> response = postController.get(POST_ID);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PostDTO result = response.getBody();
        assert result != null;
        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(CONTENT, result.getContent());

        verify(postService, times(1)).getById(POST_ID);
    }

    @Test
    void update() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        doNothing().when(postService).update(any(), any(PostCreatedDTO.class));
        ResponseEntity<Void> update = postController.update(POST_ID, postCreatedDTO);
        assertNotNull(update);
        assertEquals(HttpStatus.OK, update.getStatusCode());
        assertNull(update.getBody());

        verify(postService, times(1)).update(POST_ID, postCreatedDTO);
    }

    @Test
    void changeStatus() {
        PostCreatedDTO postCreatedDTO = new PostCreatedDTO();
        postCreatedDTO.setStatus(GeneralStatus.ACTIVE);

        doNothing().when(postService).changeStatus(POST_ID, postCreatedDTO);
        ResponseEntity<?> changeStatus = postController.changeStatus(POST_ID, postCreatedDTO);
        assertNotNull(changeStatus);
        assertEquals(HttpStatus.OK, changeStatus.getStatusCode());
        assertNull(changeStatus.getBody());

        verify(postService, times(1)).changeStatus(POST_ID, postCreatedDTO);

    }

    @Test
    void delete() {
        doNothing().when(postService).deleteById(POST_ID);
        ResponseEntity<AppResponse<String>> delete = postController.delete(POST_ID);
        assertNotNull(delete);
        assertEquals(HttpStatus.OK, delete.getStatusCode());
        assertNull(delete.getBody());

        verify(postService, times(1)).deleteById(POST_ID);
    }

    @Test
    void filter() {
        PostFilterDTO postFilterDTO = new PostFilterDTO();
        postFilterDTO.setExceptId(SET_EXCEPT_ID);
        postFilterDTO.setQuery(QUERY);

        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);
        postDTO.setContent(CONTENT);

        Page<PostDTO> pageResult = new PageImpl<>(List.of(postDTO), PageRequest.of(page, size), 1);
        Mockito.<Page<PostDTO>>when(postService.filter(eq(postFilterDTO), eq(page), eq(size))).thenReturn(pageResult);

        ResponseEntity<Page<PostDTO>> response = postController.filter(postFilterDTO, 1, 12);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PostDTO result = Objects.requireNonNull(response.getBody()).getContent().get(0);
        assertEquals(POST_ID, result.getId());
        assertEquals(TITLE, result.getTitle());
        assertEquals(CONTENT, result.getContent());

        verify(postService, times(1)).filter(postFilterDTO, page, size);
    }

    @Test
    void similarPostList() {
        SimilarPostListDTO similarPostListDTO = new SimilarPostListDTO();
        similarPostListDTO.setExceptId("exceptId");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);
        postDTO.setContent(CONTENT);

        when(postService.getSimilarPostList(similarPostListDTO)).thenReturn(List.of(postDTO));

        ResponseEntity<List<PostDTO>> response = postController.similarPostList(similarPostListDTO);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PostDTO result = Objects.requireNonNull(response.getBody()).get(0);
        assertEquals("post-123", result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());

        verify(postService, times(1)).getSimilarPostList(similarPostListDTO);
    }

    @Test
    void testFilter() {
        PostAdminFilterDTO postAdminFilterDTO = new PostAdminFilterDTO();
        postAdminFilterDTO.setPostQuery("postQuery");
        postAdminFilterDTO.setProfileQuery("profileQuery");

        PostDTO postDTO = new PostDTO();
        postDTO.setId(POST_ID);
        postDTO.setTitle(TITLE);
        postDTO.setContent(CONTENT);

        Page<PostDTO> pageResult = new PageImpl<>(List.of(postDTO), PageRequest.of(page, size), 1);
        Mockito.when(postService.adminFilter(eq(postAdminFilterDTO), eq(page), eq(size))).thenReturn(pageResult);

        ResponseEntity<Page<PostDTO>> response = postController.filter(postAdminFilterDTO, 1, 12);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        PostDTO result = Objects.requireNonNull(response.getBody()).get().toList().get(0);
        assertEquals("post-123", result.getId());
        assertEquals("title", result.getTitle());
        assertEquals("content", result.getContent());

        verify(postService, times(1)).adminFilter(postAdminFilterDTO, page, size);
    }
}