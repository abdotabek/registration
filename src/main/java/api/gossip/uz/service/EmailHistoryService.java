package api.gossip.uz.service;

import api.gossip.uz.entity.EmailHistoryEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.exception.ExceptionUtil;
import api.gossip.uz.repository.EmailHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmailHistoryService {

    private final EmailHistoryRepository emailHistoryRepository;
    private final ResourceBundleService bundleService;

    public void create(final String email, final String code, final SmsType emailType) {
        final EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setEmailType(emailType);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now());
        emailHistoryRepository.save(entity);
    }

    public Long getEmailCount(final String email) {
        final LocalDateTime now = LocalDateTime.now();
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(1), now);
    }

    public void check(final String email, final String code, AppLanguage language) {
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()) {
            throw ExceptionUtil.throwBadRequestException((bundleService.getMessage("profile.ver.failed", language)));
        }
        final EmailHistoryEntity entity = optional.get();
        if (entity.getAttemptCount() >= 3) {
            throw ExceptionUtil.throwBadRequestException(bundleService.getMessage("attempt.count", language));
        }
        //check codes
        if (!entity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(entity.getId());   //update attempt count
            throw ExceptionUtil.throwBadRequestException(bundleService.getMessage("profile.ver.failed", language));
        }
        final LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {
            throw ExceptionUtil.throwBadRequestException(bundleService.getMessage("profile.ver.failed", language));
        }
    }
}
