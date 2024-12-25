package api.gossip.uz.dto;

import api.gossip.uz.enums.ProfileRole;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileRoleDTO {
    Integer id;
    Integer profileId;
    ProfileRole roles;
    LocalDateTime createdDate;
}
