package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import yangfawu.eroster.model.PublicUser;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NonNull
    private String name, school, email, password;

    @NonNull
    private PublicUser.Role role;

}
