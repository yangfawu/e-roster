package yangfawu.eroster.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.Course;

import java.util.Optional;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {

    Optional<Course> findById(String id);

    Page<Course> findByTeacherId(String teacherId, Pageable pageable);

    Page<Course> findByStudentIdsContainingOrderByCreated(String studentId, Pageable pageable);

}
