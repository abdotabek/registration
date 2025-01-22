package api.gossip.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetPasswordConfirmDTO {

    @NotBlank(message = "Username required")
    String username;

    @NotBlank(message = "Confirm code required")
    String configCode;

    @NotBlank(message = "Password required")
    String password;
}
