package yangfawu.eroster.service;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yangfawu.eroster.model.UserCredential;

import java.util.Optional;

@Service
public class TokenAuthenticationService {

    private final UserService userSvc;
    private final JWTTokenService jwtTokenSvc;

    @Autowired
    public TokenAuthenticationService(
            UserService userSvc,
            JWTTokenService jwtTokenSvc) {
        this.userSvc = userSvc;
        this.jwtTokenSvc = jwtTokenSvc;
    }

    public Optional<UserCredential> findUserCredentialByToken(String token) {
        return Optional.ofNullable(jwtTokenSvc.verifyToken(token))
                .map(attributes -> {
                    String id = String.valueOf(attributes.get("id"));
                    String username = String.valueOf(attributes.get("username"));

                    UserCredential cred = userSvc.retrieveUserCred(id);
                    if (cred == null || !cred.getUsername().equals(username))
                        return null;
                    return cred;
                });
    }

    /**
     * Generates a new token if the user has valid credentials
     * @param username the username of the user
     * @param password the password of the user
     * @return optional token depending on validness of inputs
     */
    public Optional<String> login(String username, String password) {
        UserCredential cred = userSvc.retrieveUserCredByLogin(username, password);
        if (cred == null)
            return Optional.empty();

        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        builder.put("username", cred.getUsername());
        builder.put("id", cred.getId());
        return Optional.of(jwtTokenSvc.createNewToken(builder.build()));
    }

    public void logout(UserCredential cred) {
        throw new RuntimeException("Logging out not implemented yet.");
    }

}
