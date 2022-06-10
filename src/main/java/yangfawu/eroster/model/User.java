package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractIdEntity {

    public enum UserType {
        STUDENT,
        TEACHER
    }

    private String email, name;
    private UserType accountType;

}

