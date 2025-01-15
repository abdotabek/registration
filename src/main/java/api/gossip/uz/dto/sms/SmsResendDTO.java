package api.gossip.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsResendDTO {

    @NotBlank(message = "Phone required")
    String phone;
}
