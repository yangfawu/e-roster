package yangfawu.eroster.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.DataConflictException;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.repository.UserCredentialRepository;
import yangfawu.eroster.repository.UserRepository;

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
        if (!StringUtils.hasText(name))
            throw new IllegalArgumentException("Name is invalid.");
        if (!EmailValidator.getInstance().isValid(email))
            throw new IllegalArgumentException("Email is invalid.");
        if (password == null)
            throw new IllegalArgumentException("Password is invalid.");

        name = StringUtils.trimWhitespace(name);
        email = StringUtils.trimWhitespace(email);

        if (userRepo.existsUserByEmail(email))
            throw new DataConflictException("Email is already used by another user.");

        String id = userRepo.insert(new User(name, email)).getId();
        userCredRepo.save(new UserCredential(id, email, password));

        return userRepo.getUserById(id);
    }

    /**
     * Retrieves user by id. (Will throw if user not found)
     * @param id the id of the user
     * @return the user if found
     */
    public User retrieveUser(String id) {
        if (!StringUtils.hasText(id))
            throw new IllegalArgumentException("User ID is invalid");

        id = StringUtils.trimWhitespace(id);

        User user = userRepo.getUserById(id);
        if (user == null)
            throw new DataConflictException("User does not exist.");

        return user;
    }

    /**
     * Retrieves user credential by id. (Will throw if user not found)
     * @param id the id of the user
     * @return the user credential if found
     */
    public UserCredential retrieveUserCred(String id) {
        if (!StringUtils.hasText(id))
            throw new IllegalArgumentException("User ID is invalid");

        id = StringUtils.trimWhitespace(id);

        UserCredential cred = userCredRepo.getUserCredentialById(id);
        if (cred == null)
            throw new DataConflictException("User credential does not exist.");

        return cred;
    }

    /**
     * Retrieves user credentials by username and password
     * @param username the username of the user [the email]
     * @param password the password of the user
     * @return the user credential linked to the user
     */
    public UserCredential retrieveUserCredential(String username, String password) {
        if (!StringUtils.hasText(username))
            throw new IllegalArgumentException("Username is invalid.");
        if (password == null)
            throw new IllegalArgumentException("Password is invalid.");

        username = StringUtils.trimWhitespace(username);

        UserCredential userCred = userCredRepo.getUserCredentialByUsernameAndPassword(username, password);
        if (userCred == null)
            throw new DataConflictException("User credentials not found.");

        return userCred;
    }

    /**
     * Attempts to update user's name by id.
     * @param id the id of the user
     * @param newName the new name to be applied
     */
    public void updateUserName(String id, String newName) {
        if (!StringUtils.hasText(newName))
            throw new IllegalArgumentException("New name is invalid.");

        User user = retrieveUser(id);
        assert user != null;

        newName = StringUtils.trimWhitespace(newName);

        user.setName(newName);
        userRepo.save(user);
    }

}
