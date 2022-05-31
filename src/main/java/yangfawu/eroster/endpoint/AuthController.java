package yangfawu.eroster.endpoint;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.RefreshToken;
import yangfawu.eroster.payload.request.LoginRequest;
import yangfawu.eroster.payload.request.RefreshTokenRequest;
import yangfawu.eroster.payload.request.RegisterRequest;
import yangfawu.eroster.payload.response.JwtResponse;
import yangfawu.eroster.service.JWTTokenService;
import yangfawu.eroster.service.RefreshTokenService;
import yangfawu.eroster.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    private final RefreshTokenService refTokenSvc;
    private final JWTTokenService jwtTokenSvc;
    private final UserService userSvc;

    public AuthController(
            RefreshTokenService refTokenSvc,
            JWTTokenService jwtTokenSvc,
            UserService userSvc) {
        this.refTokenSvc = refTokenSvc;
        this.jwtTokenSvc = jwtTokenSvc;
        this.userSvc = userSvc;
    }

    private JwtResponse fromCred(PrivateUser cred) {
        final String USER_ID = cred.getPublicId();
        return new JwtResponse(
            jwtTokenSvc.createNewToken(USER_ID),
            refTokenSvc.createRefreshToken(USER_ID)
        );
    }

    @PostMapping("/register")
    public JwtResponse register(@RequestBody RegisterRequest req) {
        return fromCred(userSvc.createUser(
            req.getName(),
            req.getSchool(),
            req.getRole(),
            req.getEmail(),
            req.getPassword()
        ));
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest req) {
        PrivateUser cred;
        try {
            cred = userSvc.getPrivateUser(req.getEmail(), req.getPassword());
        } catch (Exception e) {
            throw new ForbiddenException("Check your login info.");
        }

        return fromCred(cred);
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest req) {
        RefreshToken refToken;
        try {
            refToken = refTokenSvc.findByToken(req.getToken());
        } catch (Exception e) {
            throw new ForbiddenException("Invalid refresh token.");
        }

        return Optional.of(refToken)
                        .map(refTokenSvc::verifyToken)
                        .map(RefreshToken::getUserId)
                        .map(USER_ID -> new JwtResponse(
                            jwtTokenSvc.createNewToken(USER_ID),
                            refToken.getToken()
                        ))
                        .orElseThrow(() -> new RuntimeException("Could not generate new token from refresh token."));
    }

}
