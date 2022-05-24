package yangfawu.eroster.endpoint;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.exception.DataConflictException;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.payload.Credential;
import yangfawu.eroster.service.TokenAuthenticationService;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final TokenAuthenticationService tokenAuthSvc;
    private final UserService userSvc;

    public AuthController(
            TokenAuthenticationService tokenAuthSvc,
            UserService userSvc) {
        this.tokenAuthSvc = tokenAuthSvc;
        this.userSvc = userSvc;
    }

    @PostMapping("/register")
    public String register(@RequestBody Credential request) {
        String userId = userSvc.createUser(
            request.getName(),
            request.getEmail(),
            request.getPassword()
        ).getId();
        UserCredential cred = userSvc.retrieveUserCred(userId);
        return tokenAuthSvc.login(cred.getUsername(), cred.getPassword())
                .orElseThrow(() -> new DataConflictException("Invalid username and/or password."));
    }

    @PostMapping("/login")
    public String login(@RequestBody Credential request) {
        return tokenAuthSvc.login(request.getEmail(), request.getPassword())
                .orElseThrow(() -> new DataConflictException("Invalid username and/or password."));
    }

}
