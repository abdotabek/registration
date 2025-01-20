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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsSendService {

    final RestTemplate restTemplate;
    final SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;
    final SmsHistoryService smsHistoryService;
    final ResourceBundleService bundleService;
    @Value("${eskiz.url}")
    String smsURL;
    @Value("${eskiz.login}")
    String accountLogin;
    @Value("${eskiz.password}")
    String accountPassword;


    private void sendSms(String phoneNumber, String message, String code, SmsType smsType) {
        //check
        Long count = smsHistoryService.getSmsCount(phoneNumber);
        Long smsLimit = 3L;
        if (count >= smsLimit) {
            System.out.println("-----------Limit reached. Phone: " + phoneNumber);
            throw new RuntimeException("Sms limit reached");
        }
        //send
        sendSms(phoneNumber, message);
        //save
        smsHistoryService.create(phoneNumber, message, code, smsType);
    }

    public void sendRegistration(String phoneNumber, AppLanguage language) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.config.code", language);
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.REGISTRATION);

    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {
        //get token
        String token = getToken();
        //send sms
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);

        SmsRequestDTO smsRequestDTO = new SmsRequestDTO();
        smsRequestDTO.setMobile_phone(phoneNumber);
        smsRequestDTO.setMessage(message);
        smsRequestDTO.setFrom("4546");

        HttpEntity<SmsRequestDTO> httpEntity = new HttpEntity<>(smsRequestDTO, headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response =
                    restTemplate.exchange(smsURL + "/message/sms/send", HttpMethod.POST, httpEntity, SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        if (optional.isEmpty()) {
            String token = getTokenFromProvider();
            SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            return token;
        }
        //id exist check it
        SmsProviderTokenHolderEntity entity = optional.get();
        if (LocalDateTime.now().isBefore(entity.getExpiredDate())) {    //if not expired
            return entity.getToken();
        }
        // get new token and updated
        String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
        smsProviderTokenHolderRepository.save(entity);
        return token;

    }

    private String getTokenFromProvider() {
        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);

        try {
            System.out.println("---- Sms Sender new Token was token ----");
            SmsAuthResponseDTO response = restTemplate.postForObject(smsURL + "/auth/login", smsAuthDTO, SmsAuthResponseDTO.class);
            return response.getData().getToken();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }

    }
}
