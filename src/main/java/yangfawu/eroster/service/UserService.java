package yangfawu.eroster.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.repository.UserCredentialRepository;
import yangfawu.eroster.repository.UserRepository;
import yangfawu.eroster.util.ServiceUtil;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepo;
    private final UserCredentialRepository userCredRepo;

    @Autowired
    public UserService(
            UserRepository userRepo,
            UserCredentialRepository userCredRepo) {
        this.userRepo = userRepo;
        this.userCredRepo = userCredRepo;
    }

    /**
     * Attempts to create a new user.
     * @param name name of new user
     * @param email email of new user
     * @return the created user
     */
    public User createUser(String name, String email, String password) {
        final String EMAIL = StringUtils.trimWhitespace(email);
        if (email == null ||
            EmailValidator.getInstance().isValid(EMAIL) ||
            password == null ||
            userRepo.existsUserByEmail(EMAIL))
            return null;

        final String USER_ID = userRepo.insert(new User(
            ServiceUtil.cleanOrDefault(name, "Anonymous"),
            email
        )).getId();
        userCredRepo.save(new UserCredential(USER_ID, EMAIL, password));

        return userRepo.getUserById(USER_ID);
    }

    /**
     * Retrieves user by ID.
     * @param id the id of the user
     * @return the user
     */
    public User retrieveUser(String id) {
        if (id == null)
            return null;
        return userRepo.getUserById(StringUtils.trimWhitespace(id));
    }

    /**
     * Retrieves user credential by id.
     * @param id the id of the user
     * @return the user credential
     */
    public UserCredential retrieveUserCred(String id) {
        if (id == null)
            return null;
        return userCredRepo.getUserCredentialById(StringUtils.trimWhitespace(id));
    }

    /**
     * Retrieves user credentials by username and password
     * @param username the username of the user [the email]
     * @param password the password of the user
     * @return the user credential linked to the user
     */
    public UserCredential retrieveUserCredByLogin(String username, String password) {
        if (username == null || password == null)
            return null;
        return userCredRepo.getUserCredentialByUsernameAndPassword(
            StringUtils.trimWhitespace(username),
            password
        );
    }

    /**
     * Attempts to update user's name by id.
     * @param id the id of the user
     * @param newName the new name to be applied
     * @return whether operation was done or not
     */
    public boolean updateUserName(String id, String newName) {
        if (!StringUtils.hasText(newName))
            return false;

        User user = retrieveUser(id);
        if (user == null)
            return false;

        user.setName(StringUtils.trimWhitespace(newName));
        userRepo.save(user);
        return true;
    }

    protected boolean updateUser(User user) {
        if (user == null)
            return false;
        userRepo.save(user);
        return true;
    }

    protected boolean updateUserCred(UserCredential cred) {
        if (cred == null)
            return false;
        userCredRepo.save(cred);
        return true;
    }

}
