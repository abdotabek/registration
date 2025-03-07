package api.gossip.uz.dto.profile;

import api.gossip.uz.enums.GeneralStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileStatusDTO {

    GeneralStatus status;

}
