package api.gossip.uz.util;

import java.util.regex.Pattern;

public class EmailUtil {
    public static boolean isEmail(final String value) {
        final String emailRegex = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
        return Pattern.matches(emailRegex, value);
    }
}
