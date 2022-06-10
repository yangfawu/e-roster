package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.repository.UserRepository;

import java.time.Instant;

@Service
public class InvitationsCollectionService extends AbstractListCollectionService<Connection> {

    @Autowired
    public InvitationsCollectionService(UserRepository userRepo) {
        super(Connection.class, userRepo, "invitations");
    }

    @Override
    public void addReference(String rootId, String reference) {
        addItem(
                rootId,
                Connection.builder()
                        .ref(reference)
                        .created(Instant.now())
                        .build()
        );
    }

}
