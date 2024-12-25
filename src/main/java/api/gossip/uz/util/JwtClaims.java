package api.gossip.uz.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PUBLIC)
public class JwtClaims {
    static String USERNAME = "username,";
    static String ROLE = "role";
}
