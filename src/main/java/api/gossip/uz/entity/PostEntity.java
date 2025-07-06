package api.gossip.uz.entity;

import api.gossip.uz.enums.GeneralStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "post")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "title")
    String title;

    @Column(name = "content", columnDefinition = "text")
    String content;

    @Column(name = "profile_id")
    Integer profileId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    ProfileEntity profile;

    @Column(name = "photo_id")
    String photoId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "photo_id", insertable = false, updatable = false)
    AttachEntity photo;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "visible")
    Boolean visible = true;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    GeneralStatus status;

}
