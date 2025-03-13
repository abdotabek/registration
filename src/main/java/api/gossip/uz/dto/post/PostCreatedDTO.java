package api.gossip.uz.dto.post;

import api.gossip.uz.dto.AttachCreateDTO;
import api.gossip.uz.enums.GeneralStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostCreatedDTO {
    @NotBlank(message = "Title required")
    @Length(min = 5, max = 255, message = "min->5, max->255")
    String title;
    @NotBlank(message = "content required")
    String content;
    @NotNull(message = "Photo required")
    AttachCreateDTO photo;
    GeneralStatus status;
}
