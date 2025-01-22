package api.gossip.uz.entity;

import api.gossip.uz.enums.SmsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "email_history")
@Entity
public class EmailHistoryEntity extends BaseEntity {

    @Column(name = "email")
    String email;

    @Column(name = "code")
    String code;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "email_type")
    SmsType emailType;

    @Column(name = "attempt_count")
    Integer attemptCount = 0;

}
