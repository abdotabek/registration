package api.gossip.uz.dto.jwt;

import api.gossip.uz.enums.ProfileRole;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtDTO {
    Integer id;
    List<ProfileRole> roleList;
}
