package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/private/user")
@Log4j2
public class UserController {

    private final UserService userSvc;

    @Autowired
    public UserController(UserService userSvc) {
        this.userSvc = userSvc;
    }

    @GetMapping("")
    public PublicUser getSelf(@AuthenticationPrincipal PrivateUser cred) {
        return userSvc.getPublicUser(cred.getPublicId());
    }

}
