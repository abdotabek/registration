package api.gossip.uz.service;

import api.gossip.uz.enums.AppLanguage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;

/*@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)*/
@Component
public class ResourceBundleService {

    private static ResourceBundleMessageSource bundleMessage;

    @Autowired
    ResourceBundleService(ResourceBundleMessageSource messageSource) {
        ResourceBundleService.bundleMessage = messageSource;
    }

    public String getMessage(String msCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return bundleMessage.getMessage(msCode, args, locale);
    }


    /* public String getMessage(String code, AppLanguage language) {
         return bundleMessage.getMessage(code, null, new Locale(language.name()));
     }*/


    /* @Autowired
    LocalizationService(ResourceBundleMessageSource messageSource) {
        LocalizationService.messageSource = messageSource;
    }

    public String getMessage(String msgCode, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, args, locale);
    }*/

}
