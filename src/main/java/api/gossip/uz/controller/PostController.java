package api.gossip.uz.controller;

import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<PostDTO>> profilePostList() {
        return ResponseEntity.ok(postService.getProfilePostList());
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Get post by id", description = "Api used for postList")
    public ResponseEntity<PostDTO> get(@PathVariable("id") String id) {
        return ResponseEntity.ok(postService.getById(id));
    }


}
