package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yangfawu.eroster.model.Attendance;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    public Attendance getAttendanceById(String id);

}
