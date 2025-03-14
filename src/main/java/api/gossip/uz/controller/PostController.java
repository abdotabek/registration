package api.gossip.uz.controller;

import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.dto.post.PostFilterDTO;
import api.gossip.uz.dto.post.SimilarPostListDTO;
import api.gossip.uz.dto.profile.PostAdminFilterDTO;
import api.gossip.uz.service.PostService;
import api.gossip.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/posts")
public class PostController {

    PostService postService;

    @PostMapping
    @Operation(summary = "Create post", description = "Api used for post create")
    public ResponseEntity<PostDTO> create(@RequestBody PostCreatedDTO postCreatedDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.create(postCreatedDTO));
    }

    @GetMapping
    @Operation(summary = "Get list post", description = "Api used for postList")
    public ResponseEntity<Page<PostDTO>> profilePostList(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "12") int size) {
        return ResponseEntity.ok(postService.getProfilePostList(PageUtil.page(page), size));
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id", description = "Api used for postList")
    public ResponseEntity<PostDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Put post by id", description = "Api used from post updated")
    public ResponseEntity<Void> update(@Valid @PathVariable String id, @RequestBody PostCreatedDTO createdDTO) {
        postService.update(id, createdDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post by id", description = "Api used from post deleted")
    public ResponseEntity<Void> delete(@Valid @PathVariable String id) {
        postService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/public/filter")
    @Operation(summary = "Create filter", description = "Api used from filer pagination")
    public ResponseEntity<Page<PostDTO>> filter(@Valid @RequestBody PostFilterDTO filter,
                                                @RequestParam(value = "page", defaultValue = "1") int page,
                                                @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.filter(filter, page - 1, size));
    }

    @PostMapping("/public/similar")
    @Operation(summary = "Get similar post list", description = "Api used for getting similar post list")
    public ResponseEntity<List<PostDTO>> similarPostList(@Valid @RequestBody SimilarPostListDTO similarPostListDTO) {
        return ResponseEntity.ok(postService.getSimilarPostList(similarPostListDTO));
    }

    @PostMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Post filter for admin", description = "Api used for filter post list")
    public ResponseEntity<Page<PostDTO>> fil(@RequestBody PostAdminFilterDTO filterDTO,
                                             @RequestParam(value = "page", defaultValue = "1") int page,
                                             @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(postService.adminFilter(filterDTO, page, size));
    }

}
