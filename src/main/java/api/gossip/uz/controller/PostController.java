package api.gossip.uz.controller;

import api.gossip.uz.dto.PostDTO;
import api.gossip.uz.util.SpringSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "PostController", description = "Api set for working with Post")
public class PostController {

    @PostMapping
    @Operation(summary = "Create post", description = "Api used created post")
    public String create(@RequestBody PostDTO postDTO) {
        System.out.println(SpringSecurityUtil.getCurrentProfile());
        System.out.println(SpringSecurityUtil.getCurrentProfileId());
        return "done";
    }

}
