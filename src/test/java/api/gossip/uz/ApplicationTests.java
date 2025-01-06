package api.gossip.uz;

import api.gossip.uz.enums.SmsType;
import api.gossip.uz.service.SmsSendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest

public class ApplicationTests {
    @Autowired
    private SmsSendService smsSendService;

    @Test
    void contextLoads() {
//        smsSendService.getToken();
        smsSendService.sendSms("998937877405", "Bu Eskiz dan test", SmsType.REGISTRATION);
    }
}
