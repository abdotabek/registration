package api.gossip.uz.dto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfilePasswordUpdate {

    @NotBlank(message = "oldPassword required")
    String oldPassword;

    @NotBlank(message = "newPassword code required")
    String newPassword;
}
