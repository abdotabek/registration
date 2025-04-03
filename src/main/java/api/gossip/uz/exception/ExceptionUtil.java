package api.gossip.uz.exception;

import api.gossip.uz.dto.ErrorDTO;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ExceptionUtil {
    public static NotFoundException throwNotFoundException(String message) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .title("Resource Not Found")
                .message(message)
                .status(HttpStatus.NOT_FOUND)
                .localDateTime(LocalDateTime.now())
                .build();
        throw new NotFoundException(errorDTO, message);
    }

    public static CustomIllegalArgumentException throwCustomIllegalArgumentException(String message) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .title("Bad Request")
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .localDateTime(LocalDateTime.now())
                .build();
        throw new CustomIllegalArgumentException(errorDTO, message);
    }

    public static ConflictException throwConflictException(String message) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .title("Conflict Error")
                .message(message)
                .status(HttpStatus.CONFLICT)
                .localDateTime(LocalDateTime.now())
                .build();
        throw new ConflictException(errorDTO, message);
    }

    public static BadRequestException throwBadRequestException(String message) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .title("BadRequest Error")
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .localDateTime(LocalDateTime.now())
                .build();
        throw new BadRequestException(errorDTO, message);
    }
}
