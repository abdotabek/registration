package api.gossip.uz.dto.sms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsSendResponseDTO {
    String id;
    String message;
    String status;
}
