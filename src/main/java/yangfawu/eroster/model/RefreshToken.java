package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "refresh-tokens")
@Data
public class RefreshToken {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId, token;

    private Instant expiration;

    public RefreshToken(String userId, String token, Instant expiration) {
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
    }
}
