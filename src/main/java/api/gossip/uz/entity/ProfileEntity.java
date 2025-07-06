package api.gossip.uz.entity;

import api.gossip.uz.enums.GeneralStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "photo_id")
    String photoId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    AttachEntity attachEntity;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    List<ProfileRoleEntity> roleeList;
}
