package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.StudentRequest;

@Repository
public interface StudentRequestRepository extends MongoRepository<StudentRequest, String> {

    boolean existsByCourseIdAndAndStudentId(String courseId, String studentId);

    long deleteByCourseIdAndStudentId(String courseId, String studentId);

}
