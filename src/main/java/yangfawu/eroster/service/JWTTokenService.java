package yangfawu.eroster.service;

import com.google.common.collect.ImmutableMap;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JWTTokenService implements Clock {

    private static final CompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

    private final String issuer, secretKey;

    @Autowired
    public JWTTokenService(Environment env) {
        this.issuer = env.getProperty("program.jwt-token-service.issuer");
        this.secretKey = TextCodec.BASE64.encode(env.getProperty("program.jwt-token-service.secret-key-food"));
    }

    public String createNewToken(Map<String, Object> attributes) {
        Claims claims = Jwts.claims()
                            .setIssuer(issuer)
                            .setIssuedAt(now());
        claims.putAll(attributes);
        return Jwts.builder()
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compressWith(COMPRESSION_CODEC)
                    .compact();
    }

    /**
     * Parses token for its claims.
     * @param token the token string
     * @return an IMMUTABLE map representing the token claims
     */
    public Map<String, Object> verifyToken(String token) {
        JwtParser parser = Jwts.parser()
                .requireIssuer(issuer)
                .setSigningKey(secretKey)
                .setClock(this);

        Claims claims;
        try {
            claims = parser.parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return ImmutableMap.of();
        }

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for (Map.Entry<String, Object> entry : claims.entrySet())
            builder.put(entry);
        return builder.build();
    }

    @Override
    public Date now() {
        return Date.from(Instant.now());
    }
}
