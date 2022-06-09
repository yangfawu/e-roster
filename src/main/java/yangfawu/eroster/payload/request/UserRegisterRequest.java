package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.User;
import yangfawu.eroster.payload.CheckablePayload;

@Data
public class UserRegisterRequest extends CheckablePayload {

    @NonNull
    private String email, password, name;

    @NonNull
    private User.UserType accountType;

    @Override
    public void clean() {
        email = StringUtils.trimWhitespace(email);
        name = StringUtils.trimWhitespace(name);
    }

    @Override
    public void check() throws InputValidationException {
        if (name.length() < 3)
            throw new InputValidationException("Name needs to be at least 3 characters.");
        if (email.length() < 3)
            throw new InputValidationException("Email is too short to be even valid.");
        if (password.length() < 8)
            throw new InputValidationException("Password needs to be at least 8 characters.");
    }
}
