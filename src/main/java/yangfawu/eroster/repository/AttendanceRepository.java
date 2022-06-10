package yangfawu.eroster.repository;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.Attendance;

@Repository
public class AttendanceRepository extends AbstractRootRepository<Attendance> {

    @Autowired
    public AttendanceRepository(Firestore db) {
        super(db, Attendance.class, "attendances");
    }

}
