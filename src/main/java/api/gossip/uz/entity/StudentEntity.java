package api.gossip.uz.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "student")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StudentEntity extends BaseEntity {

    @Column(name = "name")
    String name;

    @Column(name = "surname")
    String surname;

    @Column(name = "age")
    Long age;

}
