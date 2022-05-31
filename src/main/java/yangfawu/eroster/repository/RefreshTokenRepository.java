package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yangfawu.eroster.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {

    public long deleteByUserId(String userId);

    public Optional<RefreshToken> findByToken(String token);

}
