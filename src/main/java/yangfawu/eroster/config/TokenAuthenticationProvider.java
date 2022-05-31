package yangfawu.eroster.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import yangfawu.eroster.exception.TokenException;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.service.JWTTokenService;
import yangfawu.eroster.service.UserService;

import java.util.Optional;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final JWTTokenService jwtTokenSvc;
    private final UserService userSvc;

    public TokenAuthenticationProvider(
            JWTTokenService jwtTokenSvc,
            UserService userSvc) {
        this.jwtTokenSvc = jwtTokenSvc;
        this.userSvc = userSvc;
    }

    @Override
    protected void additionalAuthenticationChecks(
            UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // INTENTIONALLY LEFT BLANK
    }

    @Override
    protected UserDetails retrieveUser(
            String username,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        Object tokenObject = authentication.getCredentials();
        return Optional.ofNullable(tokenObject)
                .map(String::valueOf)
                .map(jwtTokenSvc::verifyToken)
                .map(claims -> {
                    String userId = claims.getId();
                    String password = claims.getSubject();
                    if (userId == null || password == null)
                        throw new TokenException("Invalid token provided.");
                    PrivateUser cred = userSvc.getPrivateUser(userId);
                    if (!cred.getPassword().equals(password))
                        throw new TokenException("Access token cannot be used anymore.");
                    return cred;
                })
                .orElseThrow(() -> new TokenException("Can't find token " + tokenObject + "."));
    }
}
