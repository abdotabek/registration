package api.gossip.uz.controller;

import api.gossip.uz.dto.AppResponse;
import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.dto.post.SimilarPostListDTO;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
import api.gossip.uz.service.PostService;
import api.gossip.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    @PostMapping
    @Operation(summary = "Create post", description = "Api used for post create")
    public ResponseEntity<PostDTO> create(@RequestBody final PostCreatedDTO postCreatedDTO) {
        log.info("Create : {} post{} status{}", postCreatedDTO.getContent(), postCreatedDTO.getPhoto(), postCreatedDTO.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postCreatedDTO));
    }

    @GetMapping
    @Operation(summary = "Get list post", description = "Api used for postList")
    public ResponseEntity<Page<PostDTO>> profilePostList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "12") int size) {
        log.info("Get posts : page={}, size={}", page, size);
        return ResponseEntity.ok(postService.getProfilePostList(PageUtil.page(page), size));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id", description = "Api used for postList")
    public ResponseEntity<PostDTO> get(@PathVariable("id") final String id) {
        log.info("Get post by id : {} id", id);
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Put post by id", description = "Api used from post updated")
    public ResponseEntity<Void> update(@Valid @PathVariable final String id, @RequestBody final PostCreatedDTO createdDTO) {
        postService.update(id, createdDTO);
        log.info("Update post : {} content, {} title", createdDTO.getContent(), createdDTO.getTitle());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "Put post change status", description = "Api used from update status")
    public ResponseEntity<Void> changeStatus(@PathVariable final String id, @RequestBody final PostCreatedDTO createdDTO) {
        postService.changeStatus(id, createdDTO);
        log.info("Change status : {} status", createdDTO.getStatus());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post by id", description = "Api used from post deleted")
    public ResponseEntity<AppResponse<String>> delete(@Valid @PathVariable final String id) {
        postService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Create filter", description = "Api used from filer pagination")
    public ResponseEntity<Page<PostDTO>> filter(@Valid @RequestBody final PostFilterDTO filter,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Create filter : {} query, {} exceptId", filter.getQuery(), filter.getExceptId());
        return ResponseEntity.ok(postService.filter(filter, PageUtil.page(page), size));
    }

    @PostMapping("/public/similar")
    @Operation(summary = "Get similar post list", description = "Api used for getting similar post list")
    public ResponseEntity<List<PostDTO>> similarPostList(@Valid @RequestBody final SimilarPostListDTO similarPostListDTO) {
        log.info("Get post list {} exceptId", similarPostListDTO.getExceptId());
        return ResponseEntity.ok(postService.getSimilarPostList(similarPostListDTO));
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Post filter for admin", description = "Api used for filter post list")
    public ResponseEntity<Page<PostDTO>> filter(@RequestBody final PostAdminFilterDTO filterDTO,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Create filter : {}profileQuery, {}postQuery", filterDTO.getProfileQuery(), filterDTO.getPostQuery());
        return ResponseEntity.ok(postService.adminFilter(filterDTO, PageUtil.page(page), size));
    }

}
