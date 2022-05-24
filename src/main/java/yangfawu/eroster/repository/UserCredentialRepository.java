package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yangfawu.eroster.model.UserCredential;

public interface UserCredentialRepository extends MongoRepository<UserCredential, String> {

    public UserCredential getUserCredentialByUsernameAndPassword(String username, String password);

    public UserCredential getUserCredentialById(String id);

}
