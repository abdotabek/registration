package api.gossip.uz.service;

import api.gossip.uz.entity.EmailHistoryEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.repository.EmailHistoryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EmailHistoryService {

    EmailHistoryRepository emailHistoryRepository;
    ResourceBundleService bundleService;

    public void create(String email, String code, SmsType emailType) {
        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setEmailType(emailType);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now());
        emailHistoryRepository.save(entity);
    }

    public Long getEmailCount(String email) {
        LocalDateTime now = LocalDateTime.now();
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(1), now);
    }

    public void check(String email, String code, AppLanguage language) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()) {
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
        EmailHistoryEntity entity = optional.get();
        if (entity.getAttemptCount() >= 3){
            throw new RuntimeException(bundleService.getMessage("attempt.count", language));
        }
        //check codes
        if (!entity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(entity.getId());   //update attempt count
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw new RuntimeException(bundleService.getMessage("profile.ver.failed", language));
        }
    }
}
