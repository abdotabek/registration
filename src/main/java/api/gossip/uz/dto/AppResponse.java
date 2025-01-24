package api.gossip.uz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppResponse<T> {

    T data;
    String message;

    public AppResponse(T data) {
        this.data = data;
    }

    public AppResponse(String message) {
        this.message = message;
    }

    public AppResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
