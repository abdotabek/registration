package api.gossip.uz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "attach")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachEntity {

    @Id
    String id;

    @Column(name = "path")
    String path;

    @Column(name = "extension")
    String extension;

    @Column(name = "origin_name")
    String originName;

    @Column(name = "size")
    Long size;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "visible")
    Boolean visible = true;
}
