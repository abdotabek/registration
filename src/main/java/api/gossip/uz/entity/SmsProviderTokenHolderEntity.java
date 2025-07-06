package api.gossip.uz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "sms_provider_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class SmsProviderTokenHolderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Integer id;

    @Column(name = "token", columnDefinition = "text")
    String token;

    @Column(name = "created_date")
    LocalDateTime createdDate;

    @Column(name = "expired_date")
    LocalDateTime expiredDate;
}
