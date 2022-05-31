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
    private String token;

    @Indexed(unique = true)
    private String userId;

    private Instant expiration;

    public RefreshToken(String userId, Instant expiration) {
        this.userId = userId;
        this.expiration = expiration;
    }
}
