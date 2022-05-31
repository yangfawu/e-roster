package yangfawu.eroster.payload.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class RefreshTokenRequest {

    @NonNull
    private String token;

}
