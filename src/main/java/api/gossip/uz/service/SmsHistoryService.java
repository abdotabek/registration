package api.gossip.uz.service;

import api.gossip.uz.entity.SmsHistoryEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.repository.SmsHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SmsHistoryService {

    private final SmsHistoryRepository smsHistoryRepository;
    private final ResourceBundleService bundleService;

    public void create(final String phoneNumber, final String message, final String code, final SmsType smsType) {
        final SmsHistoryEntity smsHistoryEntity = new SmsHistoryEntity();
        smsHistoryEntity.setPhone(phoneNumber);
        smsHistoryEntity.setMessage(message);
        smsHistoryEntity.setCode(code);
        smsHistoryEntity.setSmsType(smsType);
        smsHistoryEntity.setAttemptCount(0);
        smsHistoryEntity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(smsHistoryEntity);
    }

    public Long getSmsCount(final String phone) {
        final LocalDateTime now = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phone, now.minusMinutes(2), now);
    }

    public void check(final String phoneNumber, final String code, AppLanguage language) {
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phoneNumber);
        if (optional.isEmpty()) {
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
        final SmsHistoryEntity entity = optional.get();
        //attempt count
        if (entity.getAttemptCount() >= 3) {
            throw new RuntimeException(bundleService.getMessage("attempt.count", language));
        }
        //check codes
        if (!entity.getCode().equals(code)) {
            smsHistoryRepository.updateAttemptCount(entity.getId());   //update attempt count
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
        //check time
        final LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
    }
}

