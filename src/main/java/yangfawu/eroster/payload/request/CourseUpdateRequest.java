package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.payload.CheckablePayload;

@Data
public class CourseUpdateRequest extends CheckablePayload {

    @NonNull
    private String id;

    private String name, description;

    @Override
    public void clean() {
        id = StringUtils.trimWhitespace(id);
        name = StringUtils.trimWhitespace(name);
        description = StringUtils.trimWhitespace(description);
    }

    @Override
    public void check() throws InputValidationException {
        if (id.length() < 1)
            throw new InputValidationException("Course ID is invalid.");
        if (name != null && name.length() < 3)
            throw new InputValidationException("Course name cannot be blank");
        if (description != null && description.length() < 1)
            throw new InputValidationException("Course description cannot be blank.");
    }
}
