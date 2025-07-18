package api.gossip.uz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

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
