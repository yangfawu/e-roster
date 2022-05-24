package yangfawu.eroster.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import yangfawu.eroster.service.TokenAuthenticationService;

import java.util.Optional;

@Component
public class TokenAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private final TokenAuthenticationService tokenAuthSvc;

    @Autowired
    public TokenAuthenticationProvider(TokenAuthenticationService tokenAuthSvc) {
        this.tokenAuthSvc = tokenAuthSvc;
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
        Object token = authentication.getCredentials();
        return Optional.ofNullable(token)
                    .map(String::valueOf)
                    .flatMap(tokenAuthSvc::findUserCredentialByToken)
                    .orElseThrow(() -> new UsernameNotFoundException("No user found with token " + token + "."));
    }
}
