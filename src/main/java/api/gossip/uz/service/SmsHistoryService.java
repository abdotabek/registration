package api.gossip.uz.service;

import api.gossip.uz.entity.SmsHistoryEntity;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.repository.SmsHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SmsHistoryService {

    SmsHistoryRepository smsHistoryRepository;

    public void create(String phoneNumber, String message, SmsType smsType) {
        SmsHistoryEntity smsHistoryEntity = new SmsHistoryEntity();
        smsHistoryEntity.setPhone(phoneNumber);
        smsHistoryEntity.setMessage(message);
        smsHistoryEntity.setSmsType(smsType);
        smsHistoryEntity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(smsHistoryEntity);
    }
}
