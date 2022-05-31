package yangfawu.eroster.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.RefreshToken;
import yangfawu.eroster.payload.request.LoginRequest;
import yangfawu.eroster.payload.request.RegisterRequest;
import yangfawu.eroster.payload.request.TokenRequest;
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

    @Autowired
    public AuthController(
            RefreshTokenService refTokenSvc,
            JWTTokenService jwtTokenSvc,
            UserService userSvc) {
        this.refTokenSvc = refTokenSvc;
        this.jwtTokenSvc = jwtTokenSvc;
        this.userSvc = userSvc;
    }

    @PostMapping("/register")
    public JwtResponse register(@RequestBody RegisterRequest req) {
        PrivateUser cred = userSvc.createUser(
                req.getName(),
                req.getSchool(),
                req.getEmail(),
                req.getPassword(),
                req.getRole()
        );
        return new JwtResponse(
                jwtTokenSvc.createNewToken(cred),
                refTokenSvc.createRefreshToken(cred.getPublicId())
        );
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody LoginRequest req) {
        PrivateUser cred;
        try {
            cred = userSvc.getPrivateUser(req.getEmail(), req.getPassword());
        } catch (Exception e) {
            throw new ForbiddenException("Check your login info.");
        }
        return new JwtResponse(
                jwtTokenSvc.createNewToken(cred),
                refTokenSvc.createRefreshToken(cred.getPublicId())
        );
    }

    @PostMapping("/refresh")
    public JwtResponse refreshToken(@RequestBody TokenRequest req) {
        RefreshToken refToken = refTokenSvc.findByToken(req.getToken());
        return Optional.of(refToken)
                .map(refTokenSvc::verifyToken)
                .map(RefreshToken::getUserId)
                .map(userSvc::getPrivateUser)
                .map(cred -> new JwtResponse(
                        jwtTokenSvc.createNewToken(cred),
                        refToken.getToken()
                ))
                .orElseThrow(() -> new RuntimeException("Could not generate new token from refresh token."));
    }

}
