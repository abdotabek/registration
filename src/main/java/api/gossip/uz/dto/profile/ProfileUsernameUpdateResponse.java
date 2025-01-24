package api.gossip.uz.dto.profile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileUsernameUpdateResponse {

    String message;
    String jwt;
}

