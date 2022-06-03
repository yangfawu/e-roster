package yangfawu.eroster.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;

public class ServiceUtil {

    public static String cleanOrDefault(String input, String backup) {
        if (StringUtils.hasText(input))
            return StringUtils.trimWhitespace(input);
        return backup;
    }

    public static Pageable generatePageable(int page, int size, Sort sort) {
        if (page < 1)
            throw new InputValidationException("Page numbers are 1-indexed.");
        if (size < 1)
            throw new InputValidationException("Page size has to be at least 1.");
        if (sort == null)
            return PageRequest.of(page - 1, size);
        return PageRequest.of(page - 1, size, sort);
    }

    public static Pageable generatePageable(int page, int size) {
        return generatePageable(page - 1, size, null);
    }

}
