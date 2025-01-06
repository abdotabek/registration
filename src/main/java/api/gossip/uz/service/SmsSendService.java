package api.gossip.uz.service;

import api.gossip.uz.dto.sms.SmsAuthDTO;
import api.gossip.uz.dto.sms.SmsAuthResponseDTO;
import api.gossip.uz.dto.sms.SmsRequestDTO;
import api.gossip.uz.dto.sms.SmsSendResponseDTO;
import api.gossip.uz.entity.SmsProviderTokenHolderEntity;
import api.gossip.uz.enums.SmsType;
import api.gossip.uz.repository.SmsProviderTokenHolderRepository;
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
    @Value("${eskiz.url}")
    String smsURL;
    @Value("${eskiz.login}")
    String accountLogin;
    @Value("${eskiz.password}")
    String accountPassword;


    public SmsSendResponseDTO sendSms(String phoneNumber, String message, SmsType smsType) {
        SmsSendResponseDTO result = sendSms(phoneNumber, message, smsType);
        smsHistoryService.create(phoneNumber, message, smsType);
        return result;
    }

    public SmsSendResponseDTO sendSms(String phoneNumber, String message) {
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
            String url = smsURL + "/message/sms/send";
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getToken() {
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

    public String getTokenFromProvider() {
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
