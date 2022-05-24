package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/private/user")
@Log4j2
public class UserController {

    private final UserService userSvc;

    public UserController(UserService userSvc) {
        this.userSvc = userSvc;
    }

    @GetMapping("")
    public User getSelf(UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        return userSvc.retrieveUser(cred.getId());
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id, UsernamePasswordAuthenticationToken user) {
        User fetchedUser = userSvc.retrieveUser(id);
        assert fetchedUser != null;

        // discard data if fetched user is not themselves
        // NOTE: token name == user email
        if (!fetchedUser.getEmail().equals(user.getName())) {
            fetchedUser.setCourses(null);
            fetchedUser.setInvites(null);
            fetchedUser.setRequests(null);
        }

        return fetchedUser;
    }

}
