package api.gossip.uz.entity;

import api.gossip.uz.enums.SmsType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "sms_history")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "phone")
    String phone;

    @Column(name = "message", columnDefinition = "text")
    String message;

    @Column(name = "code")
    String code;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "sms_type")
    @Enumerated(EnumType.STRING)
    SmsType smsType;

    @Column(name = "attempt_count")
    Integer attemptCount = 0;

}
