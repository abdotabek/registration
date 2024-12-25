package api.gossip.uz.exception;

import api.gossip.uz.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handlerBadRequestException(NotFoundException ex) {
        return new ResponseEntity<>(ex.getErrorDTO(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomIllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handlerBadRequestException(CustomIllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getErrorDTO(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDTO> handlerConflictException(ConflictException ex) {
        return new ResponseEntity<>(ex.getErrorDTO(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handlerConflictException(RuntimeException ex) {
        ex.printStackTrace();
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}
