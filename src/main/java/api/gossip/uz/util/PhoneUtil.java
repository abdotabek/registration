package api.gossip.uz.util;

import java.util.regex.Pattern;

public class PhoneUtil {
    public static boolean isPhone(final String value) {
        final String phoneRegex = "^998\\d{9}$";
        return Pattern.matches(phoneRegex, value);
    }
}
