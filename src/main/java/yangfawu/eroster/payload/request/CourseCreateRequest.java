package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.payload.CheckablePayload;

@Data
public class CourseCreateRequest extends CheckablePayload {

    private String name, description;

    @NonNull
    @Override
    public void clean() {
        name = StringUtils.trimWhitespace(name);
        description = StringUtils.trimWhitespace(description);
    }

    @Override
    public void check() throws InputValidationException {
        if (name.length() < 1)
            throw new InputValidationException("Course name cannot be blank");
        if (description.length() < 1)
            throw new InputValidationException("Course description cannot be blank.");
    }
}
