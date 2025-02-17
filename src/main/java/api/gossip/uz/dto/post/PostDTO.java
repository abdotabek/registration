package api.gossip.uz.dto.post;

import api.gossip.uz.dto.AttachDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDTO {

    String id;
    String title;
    String content;
    AttachDTO photo;
    LocalDateTime createdDate;
}
