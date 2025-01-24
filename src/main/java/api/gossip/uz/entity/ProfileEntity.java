package api.gossip.uz.entity;

import api.gossip.uz.enums.GeneralStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileEntity extends BaseEntity {

    @Column(name = "name")
    String name;

    @Column(name = "username")
    String username;    //email/phone

    @Column(name = "tamp_username")
    String tempUsername;    //email/phone

    @Column(name = "password")
    String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    GeneralStatus status;

    @Column(name = "visible")
    Boolean visible = Boolean.TRUE;

    @Column(name = "created_date")
    LocalDateTime createdDate;

}
