package api.gossip.uz.exception;

import api.gossip.uz.dto.ErrorDTO;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BadRequestException extends RuntimeException {
    ErrorDTO errorDTO;

    public BadRequestException(ErrorDTO error, String message) {
        super(message);
        this.errorDTO = error;
    }
}
