package api.gossip.uz.dto;

import api.gossip.uz.enums.ProfileRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO {

    String name;
    String username; //email/phone
    List<ProfileRole> roleList;
    String jwt;
    AttachDTO photo;

}
