package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.TokenException;
import yangfawu.eroster.model.RefreshToken;
import yangfawu.eroster.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt-token-service.refresh-expiration-ms}")
    private long tokenDuration;
    private final UserService userSvc;
    private final RefreshTokenRepository refTokenRepo;

    @Autowired
    public RefreshTokenService(
            UserService userSvc,
            RefreshTokenRepository refTokenRepo) {
        this.userSvc = userSvc;
        this.refTokenRepo = refTokenRepo;
    }

    public String createRefreshToken(String userId) {
        if (!userSvc.userExistsById(userId))
            throw new TokenException("Couldn't find user.");
        refTokenRepo.deleteByUserId(userId);
        return refTokenRepo.save(
            new RefreshToken(
                userId,
                UUID.randomUUID().toString(),
                Instant.now().plusMillis(tokenDuration)
            )
        ).getToken();
    }

    public RefreshToken verifyToken(RefreshToken token) {
        if (token.getExpiration().compareTo(Instant.now()) < 0) {
            refTokenRepo.deleteById(token.getId());
            throw new TokenException("Refresh token has expired. Please sign in again for new token.");
        }
        return token;
    }

    public RefreshToken findByToken(String token) {
        return refTokenRepo.findByToken(token).orElseThrow();
    }

}
