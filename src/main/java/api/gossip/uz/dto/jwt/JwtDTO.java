package api.gossip.uz.dto.jwt;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtDTO {
    String userName;
    String role;
    String tokenType;

    public JwtDTO(String userName, String role, String tokenType) {
        this.userName = userName;
        this.role = role;
        this.tokenType = tokenType;
    }
}
