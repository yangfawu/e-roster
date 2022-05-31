package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.PrivateUser;

import java.util.Optional;

@Repository
public interface PrivateUserRepository extends MongoRepository<PrivateUser, String> {

    Optional<PrivateUser> findByEmailAndPassword(String email, String password);

    Optional<PrivateUser> findByPublicId(String publicId);

    boolean existsByEmail(String email);

}
