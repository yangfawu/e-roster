package yangfawu.eroster.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.User;
import yangfawu.eroster.payload.request.UserUpdateRequest;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/private/user")
@RequiredArgsConstructor
@Log4j2
public class UserController {

    private final UserService userSvc;

    @GetMapping("")
    public User getSelf(@AuthenticationPrincipal Jwt token) {
        return userSvc.getUserById(userSvc.getUserIdFromFirebaseJwt(token));
    }

    @GetMapping("/{uid}")
    public User getUser(@PathVariable String uid) {
        return userSvc.getUserById(StringUtils.trimWhitespace(uid));
    }

    @PostMapping("/update")
    public User updateSelf(
            @AuthenticationPrincipal Jwt token,
            @RequestBody UserUpdateRequest req) {
        req.validate();
        return userSvc.updateUser(token, req);
    }

}
