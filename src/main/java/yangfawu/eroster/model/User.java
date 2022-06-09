package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public enum UserType {
        STUDENT,
        TEACHER
    }

    private String id, email, name;
    private UserType accountType;

}

