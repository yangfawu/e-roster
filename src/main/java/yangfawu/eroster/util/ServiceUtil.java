package yangfawu.eroster.util;

import org.springframework.util.StringUtils;

public class ServiceUtil {

    public static String cleanOrDefault(String input, String backup) {
        if (StringUtils.hasText(input))
            return StringUtils.trimWhitespace(input);
        return backup;
    }

}
