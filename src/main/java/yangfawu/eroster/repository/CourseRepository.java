package yangfawu.eroster.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import yangfawu.eroster.model.Course;

public interface CourseRepository extends MongoRepository<Course, String> {

    public Course getCourseById(String id);

}
