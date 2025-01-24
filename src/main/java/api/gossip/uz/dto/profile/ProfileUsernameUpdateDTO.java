package api.gossip.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUsernameUpdateDTO {

    @NotBlank(message = "Username required")
    String username;
}
