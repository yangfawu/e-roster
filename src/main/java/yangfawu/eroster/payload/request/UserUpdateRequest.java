package yangfawu.eroster.payload.request;

import lombok.Data;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.payload.CheckablePayload;

@Data
public class UserUpdateRequest extends CheckablePayload {

    private String name;

    @Override
    public void clean() {
        name = StringUtils.trimWhitespace(name);
    }

    @Override
    public void check() throws InputValidationException {
        if (name != null && name.length() < 3)
            throw new InputValidationException("Name needs to be at least 3 characters.");
    }
}
