package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.repository.CourseRepository;

import java.time.Instant;

@Service
public class RequestsCollectionService extends AbstractListCollectionService<Connection> {

    @Autowired
    public RequestsCollectionService(CourseRepository courseRepo) {
        super(Connection.class, courseRepo, "requests");
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
