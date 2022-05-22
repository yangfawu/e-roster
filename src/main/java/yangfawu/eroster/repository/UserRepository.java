package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yangfawu.eroster.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    public boolean existsUserByEmail(String email);

    public User getUserById(String id);

}
