package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.TokenException;
import yangfawu.eroster.model.RefreshToken;
import yangfawu.eroster.repository.RefreshTokenRepository;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final UserService userSvc;
    private final RefreshTokenRepository refTokenRepo;
    @Value("${app.jwt-token-service.refresh-expiration-ms}")
    private long tokenDuration;

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
                        Instant.now().plusMillis(tokenDuration)
                )
        ).getToken();
    }

    public RefreshToken verifyToken(RefreshToken refToken) {
        if (refToken.getExpiration().compareTo(Instant.now()) < 0) {
            deleteToken(refToken.getToken());
            throw new TokenException("Refresh token has expired. Please sign in again for new token.");
        }
        return refToken;
    }

    public RefreshToken findByToken(String token) {
        return refTokenRepo.findByToken(token).orElseThrow(() -> new TokenException("Cannot find token."));
    }

    public RefreshToken findByUserId(String userId) {
        return refTokenRepo.findByUserId(userId).orElseThrow(() -> new TokenException("Cannot find token for user."));
    }

    public long deleteToken(String token) {
        return refTokenRepo.deleteByToken(token);
    }

}
