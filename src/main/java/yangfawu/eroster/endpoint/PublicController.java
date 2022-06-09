package yangfawu.eroster.endpoint;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.model.User;
import yangfawu.eroster.payload.request.UserRegisterRequest;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@Log4j2
public class PublicController {

    private final UserService userSvc;

    @PostMapping("/register")
    public User register(@RequestBody UserRegisterRequest req) {
        req.validate();
        return userSvc.register(req);
    }

}
