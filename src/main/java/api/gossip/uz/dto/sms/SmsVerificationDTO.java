package api.gossip.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsVerificationDTO {

    @NotBlank(message = "Phone required")
    String phone;

    @NotBlank(message = "Code required")
    String code;
}
