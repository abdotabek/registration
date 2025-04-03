package api.gossip.uz.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad request")
public class BadRequestException1 extends RuntimeException {
    private int errorCode = -1;
    private String solution;

    public BadRequestException1() {
        this("Произошла ошибка");
    }

    public BadRequestException1(String message, int errorCode, String solution) {
        this(message);
        this.errorCode = errorCode;
        this.solution = solution;
    }

    public BadRequestException1(String message, int errorCode) {
        this(message);
        this.errorCode = errorCode;
    }

    public BadRequestException1(String message) {
        super(message);
    }
}
