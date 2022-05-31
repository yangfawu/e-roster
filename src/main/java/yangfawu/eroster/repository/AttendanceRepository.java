package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.Attendance;

@Repository
public interface AttendanceRepository extends MongoRepository<Attendance, String> {
}
