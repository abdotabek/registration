package api.gossip.uz.controller;

import api.gossip.uz.dto.post.PostCreatedDTO;
import api.gossip.uz.dto.post.PostDTO;
import api.gossip.uz.service.PostService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/api/posts")
public class PostController {

    PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> create(@RequestBody PostCreatedDTO postCreatedDTO) {
        return ResponseEntity.ok(postService.create(postCreatedDTO));
    }


}
