package api.gossip.uz.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "post")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostEntity extends BaseEntity{


}
