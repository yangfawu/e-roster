package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.PublicUser;

@Repository
public interface PublicUserRepository extends MongoRepository<PublicUser, String> {
}
