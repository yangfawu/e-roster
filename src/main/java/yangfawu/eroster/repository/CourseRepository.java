package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import yangfawu.eroster.model.Course;

@Repository
public interface CourseRepository extends MongoRepository<Course, String> {
}
