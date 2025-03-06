package api.gossip.uz.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarPostListDTO {
    @NotBlank(message = "exceptId required")
    private String exceptId;

}
