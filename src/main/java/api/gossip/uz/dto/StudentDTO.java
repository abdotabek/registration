package api.gossip.uz.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDTO {
    private Integer id;
    @NotNull
    private String name;
    private String surname;
    private Long age;
}
