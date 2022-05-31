package yangfawu.eroster.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yangfawu.eroster.model.PrivateUser;

import java.time.Instant;
import java.util.Date;

@Service
public class JWTTokenService implements Clock {

    private static final CompressionCodec CODEC = new GzipCompressionCodec();

    @Value("${app.jwt-token-service.issuer}")
    private String issuer;

    @Value("${app.jwt-token-service.secret}")
    private String secret;

    @Value("${app.jwt-token-service.refresh-expiration-ms}")
    private long tokenDuration;

    public String createNewToken(PrivateUser cred) {
        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(now())
                .setExpiration(Date.from(Instant.now().plusMillis(tokenDuration)))
                .setId(cred.getPublicId())
                .setSubject(cred.getPassword())
                .signWith(SignatureAlgorithm.HS256, secret)
                .compressWith(CODEC)
                .compact();
    }

    public Claims verifyToken(String token) {
        return Jwts.parser()
                .requireIssuer(issuer)
                .setSigningKey(secret)
                .setClock(this)
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public Date now() {
        return Date.from(Instant.now());
    }
}
