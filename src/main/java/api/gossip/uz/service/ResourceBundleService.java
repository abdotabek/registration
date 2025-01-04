package api.gossip.uz.service;

import api.gossip.uz.enums.AppLanguage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ResourceBundleService {

    ResourceBundleMessageSource bundleMessage;

    public String getMessage(String code, AppLanguage language) {
        return bundleMessage.getMessage(code, null, new Locale(language.name()));
    }


}
