package api.gossip.uz.dto;

import api.gossip.uz.enums.GeneralStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileDTO extends CommonDTO {
    String username; //email/phone
    String password;
    GeneralStatus status;
    Boolean visible = Boolean.TRUE;
    LocalDateTime createdDate;

}
