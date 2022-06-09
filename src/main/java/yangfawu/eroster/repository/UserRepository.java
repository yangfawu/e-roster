package yangfawu.eroster.repository;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.User;

@Repository
public class UserRepository extends AbstractRootRepository<User> {

    @Autowired
    public UserRepository(Firestore db) {
        super(db, User.class, "users");
    }

}
