package yangfawu.eroster.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.DataConflictException;
import yangfawu.eroster.exception.InvalidInputException;
import yangfawu.eroster.model.User;
import yangfawu.eroster.repository.UserRepository;

@Service
@Log4j2
public class UserService {

    public final UserRepository userRepo;

    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Attempts to create a new user.
     * @param name name of new user
     * @param email email of new user
     * @return the created user
     */
    public User createUser(String name, String email) {
        if (!StringUtils.hasText(name))
            throw new InvalidInputException("Name is invalid.");
        if (!EmailValidator.getInstance().isValid(email))
            throw new InvalidInputException("Email is invalid.");

        name = StringUtils.trimWhitespace(name);
        email = StringUtils.trimWhitespace(email);

        if (userRepo.existsUserByEmail(email))
            throw new DataConflictException("Email is already used by another user.");

        String id = userRepo.insert(new User(name, email)).getId();
        log.info("Created user {}", id);

        return userRepo.getUserById(id);
    }

    /**
     * Retrieves user by id. (Will throw if user not found)
     * @param id the id of the user
     * @return the user if found
     */
    public User retrieveUser(String id) {
        if (!StringUtils.hasText(id))
            throw new InvalidInputException("User ID is invalid");

        id = StringUtils.trimWhitespace(id);

        User user = userRepo.getUserById(id);
        if (user == null)
            throw new DataConflictException("User does not exist.");

        return user;
    }

    /**
     * Attempts to update user's name by id.
     * @param id the id of the user
     * @param newName the new name to be applied
     */
    public void updateUserName(String id, String newName) {
        if (!StringUtils.hasText(newName))
            throw new InvalidInputException("New name is invalid.");

        User user = retrieveUser(id);
        assert user != null;

        newName = StringUtils.trimWhitespace(newName);

        user.setName(newName);
        userRepo.save(user);
    }

}
