package api.gossip.uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegistrationDTO {

    @NotBlank(message = "name required")
    String name;
    @NotBlank(message = "username required")
    String username;
    @NotBlank(message = "password required")
    String password;
}
