package api.gossip.uz.dto;

import api.gossip.uz.enums.GeneralStatus;
import api.gossip.uz.enums.ProfileRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProfileDTO {

    Integer id;
    String name;
    String username; //email/phone
    List<ProfileRole> roleList;
    String jwt;
    AttachDTO photo;
    GeneralStatus status;
    LocalDateTime createdDate;
    Long postCount;

}
