package api.gossip.uz.entity;

import api.gossip.uz.enums.ProfileRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "profile_role")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProfileRoleEntity extends BaseEntity {

    @Column(name = "profile_id")
    Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    ProfileEntity profile;

    @Column(name = "roles")
    @Enumerated(EnumType.STRING)
    ProfileRole roles;

    @Column(name = "created_date")
    LocalDateTime createdDate;

}
