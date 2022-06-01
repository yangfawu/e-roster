package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.RefreshToken;
import yangfawu.eroster.payload.request.ChangePasswordRequest;
import yangfawu.eroster.payload.request.TokenRequest;
import yangfawu.eroster.payload.request.UserInfoRequest;
import yangfawu.eroster.payload.response.DetailedUserResponse;
import yangfawu.eroster.payload.response.IUserResponse;
import yangfawu.eroster.payload.response.JwtResponse;
import yangfawu.eroster.payload.response.SimpleUserResponse;
import yangfawu.eroster.service.JWTTokenService;
import yangfawu.eroster.service.RefreshTokenService;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/private/user")
@Log4j2
public class UserController {

    private final UserService userSvc;
    private final RefreshTokenService refTokenSvc;
    private final JWTTokenService jwtTokenSvc;

    @Autowired
    public UserController(
            UserService userSvc,
            RefreshTokenService refTokenSvc, JWTTokenService jwtTokenSvc) {
        this.userSvc = userSvc;
        this.refTokenSvc = refTokenSvc;
        this.jwtTokenSvc = jwtTokenSvc;
    }

    @GetMapping("")
    public IUserResponse getUser(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestParam(required = false) String id) {
        if (id == null)
            return DetailedUserResponse.from(
                    userSvc.getPublicUser(cred.getPublicId()),
                    cred.getEmail()
            );
        return SimpleUserResponse.from(userSvc.getPublicUser(id));
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestBody TokenRequest req) {
        RefreshToken refToken = refTokenSvc.findByToken(req.getToken());
        if (!cred.getPublicId().equals(refToken.getUserId()))
            throw new ForbiddenException("Can only logout your own refresh token.");
        refTokenSvc.deleteToken(refToken.getToken());
    }

    @PostMapping("/update-password")
    public JwtResponse updatePassword(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestBody ChangePasswordRequest req) {
        if (req.getOldPassword().equals(req.getNewPassword()))
            throw new ForbiddenException("New password cannot be the same as the current one.");
        if (!cred.getPassword().equals(req.getOldPassword()))
            throw new ForbiddenException("Current password is not correct.");
        PrivateUser newCred = userSvc.changePassword(cred.getPublicId(), req.getNewPassword());
        RefreshToken refToken = refTokenSvc.findByUserId(cred.getPublicId());
        return new JwtResponse(
                jwtTokenSvc.createNewToken(newCred),
                refToken.getToken()
        );
    }

    @PostMapping("/update")
    public void updateSelf(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestBody UserInfoRequest req) {
        userSvc.updateInfo(
                cred.getPublicId(),
                req.getName(),
                req.getSchool()
        );
    }

}
