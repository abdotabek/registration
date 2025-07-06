package api.gossip.uz.service;

import api.gossip.uz.dto.sms.SmsAuthDTO;
import api.gossip.uz.dto.sms.SmsAuthResponseDTO;
import api.gossip.uz.dto.sms.SmsRequestDTO;
import api.gossip.uz.dto.sms.SmsSendResponseDTO;
import api.gossip.uz.entity.SmsProviderTokenHolderEntity;
import api.gossip.uz.enums.AppLanguage;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.repository.SmsProviderTokenHolderRepository;
import api.gossip.uz.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsSendService {

    private final RestTemplate restTemplate;
    private final SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;
    private final SmsHistoryService smsHistoryService;
    private final ResourceBundleService bundleService;
    @Value("${eskiz.url}")
    private String smsURL;
    @Value("${eskiz.login}")
    private String accountLogin;
    @Value("${eskiz.password}")
    private String accountPassword;


    private void sendSms(final String phoneNumber, final String message, final String code, final SmsType smsType) {
        //check
        final Long count = smsHistoryService.getSmsCount(phoneNumber);
        final Long smsLimit = 3L;
        if (count >= smsLimit) {
            System.out.println("-----------Limit reached. Phone: " + phoneNumber);
            log.warn("Sms Limit reached. Phone: " + phoneNumber);
            throw new RuntimeException("Sms limit reached");
        }
        //send
        final SmsSendResponseDTO result = sendSms(phoneNumber, message);
        //save
        smsHistoryService.create(phoneNumber, message, code, smsType);
    }

    public void sendRegistration(final String phoneNumber, AppLanguage language) {
        final String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.config.code", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.REGISTRATION);

    }

    public void sendResetPasswordSms(final String phoneNumber, AppLanguage language) {
        final String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.reset.password.confirm", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.RESET_PASSWORD);
    }

    public void sendUsernameChangeConfirmSms(final String phoneNumber, AppLanguage language) {
        final String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.change.username.confirm", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.CHANGE_USERNAME_CONFIRM);
    }

    private SmsSendResponseDTO sendSms(final String phoneNumber, final String message) {
        //get token
        final String token = getToken();
        //send sms
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        final SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMobile_phone(phoneNumber);
        smsRequestDTO.setMessage(message);
        smsRequestDTO.setFrom("4546");

        HttpEntity<SmsRequestDTO> httpEntity = new HttpEntity<>(smsRequestDTO, headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response =
                restTemplate.exchange(smsURL + "/message/sms/send", HttpMethod.POST, httpEntity, SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            log.error("Send sms phone: {}, message: {}", phoneNumber, message);
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        if (optional.isEmpty()) {
            final String token = getTokenFromProvider();
            final SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            return token;
        }
        //id exist check it
        final SmsProviderTokenHolderEntity entity = optional.get();
        if (LocalDateTime.now().isBefore(entity.getExpiredDate())) {    //if not expired
            return entity.getToken();
        }
        // get new token and updated
        final String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
        smsProviderTokenHolderRepository.save(entity);
        return token;

    }

    private String getTokenFromProvider() {
        final SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);

        try {
            System.out.println("---- Sms Sender new Token was token ----");
            final SmsAuthResponseDTO response = restTemplate.postForObject(smsURL + "/auth/login", smsAuthDTO, SmsAuthResponseDTO.class);
            assert response != null;
            return response.getMessage().getToken();
        } catch (RuntimeException e) {
            log.error("Get token account: {}, error {}", accountLogin, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
