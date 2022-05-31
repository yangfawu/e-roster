package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class RegisterRequest {

    @NonNull
    private String name, school, role, email, password;

}
