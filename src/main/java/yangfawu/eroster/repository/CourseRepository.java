package yangfawu.eroster.repository;

import com.google.cloud.firestore.Firestore;
import org.springframework.beans.factory.annotation.Autowired;
import yangfawu.eroster.model.Course;

public class CourseRepository extends AbstractRootRepository<Course> {

    @Autowired
    public CourseRepository(Firestore db) {
        super(db, Course.class, "courses");
    }

}
