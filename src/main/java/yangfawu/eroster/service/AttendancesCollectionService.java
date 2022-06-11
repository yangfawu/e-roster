package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.CourseRepository;

import java.time.Instant;

public class AttendancesCollectionService extends AbstractListCollectionService<ListReferenceItem> {

    @Autowired
    public AttendancesCollectionService(CourseRepository courseRepo) {
        super(ListReferenceItem.class, courseRepo, "attendances");
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
