package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NonNull;

@Data
public class ChangePasswordRequest {

    @NonNull
    private String oldPassword, newPassword;

}
