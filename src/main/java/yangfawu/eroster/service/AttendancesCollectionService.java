package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.AttendanceRepository;

import java.time.Instant;

public class AttendancesCollectionService extends AbstractListCollectionService<ListReferenceItem> {

    @Autowired
    public AttendancesCollectionService(AttendanceRepository attRepo) {
        super(ListReferenceItem.class, attRepo, "attendances");
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
