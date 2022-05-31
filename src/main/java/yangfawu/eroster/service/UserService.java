package yangfawu.eroster.service;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.repository.PrivateUserRepository;
import yangfawu.eroster.repository.PublicUserRepository;
import yangfawu.eroster.util.UiAvatar;

@Service
public class UserService {

    private final PublicUserRepository pubUserRepo;
    private final PrivateUserRepository priUserRepo;

    @Autowired
    public UserService(
            PublicUserRepository pubUserRepo,
            PrivateUserRepository priUserRepo) {
        this.pubUserRepo = pubUserRepo;
        this.priUserRepo = priUserRepo;
    }

    public boolean userExistsById(String id) {
        return pubUserRepo.existsById(id);
    }

    public PublicUser getPublicUser(String id) {
        return pubUserRepo.findById(id).orElseThrow();
    }

    public PrivateUser getPrivateUser(String email, String password) {
        return priUserRepo.findByEmailAndPassword(email, password).orElseThrow();
    }

    public PrivateUser getPrivateUser(String userId) {
        return priUserRepo.findByPublicId(userId).orElseThrow();
    }

    public PrivateUser createUser(String name, String school, String role, String email, String password) {
        // prettify input data
        name = StringUtils.trimWhitespace(name);
        school = StringUtils.trimWhitespace(school);
        role = StringUtils.trimWhitespace(role);
        email = StringUtils.trimWhitespace(email);

        // validate the data
        if (name.length() < 3)
            throw new InputValidationException("Name must be at least 3 characters.");
        if (!StringUtils.hasText(school))
            throw new InputValidationException("School name not provided.");
        if (!EmailValidator.getInstance().isValid(email))
            throw new InputValidationException("Invalid email provided.");
        if (priUserRepo.existsByEmail(email))
            throw new InputValidationException("Email already taken.");
        PublicUser.Role userRole;
        try {
            userRole = PublicUser.Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            throw new InputValidationException("Invalid school role provided.");
        }

        // create user without any credentials
        PublicUser user = new PublicUser(name,school, userRole);
        user.setAvatarUrl(UiAvatar.create(name));
        user = pubUserRepo.save(user);

        // create user credentials
        PrivateUser cred = new PrivateUser(user.getId(), email, password);
        cred = priUserRepo.save(cred);

        // hook credentials ID to public
        user.setPrivateUserId(cred.getId());
        pubUserRepo.save(user);

        return cred;
    }

}
