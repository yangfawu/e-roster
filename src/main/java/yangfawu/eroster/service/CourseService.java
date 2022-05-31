package yangfawu.eroster.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepo;

    public CourseService(CourseRepository courseRepo) {
        this.courseRepo = courseRepo;
    }

    public String createCourse(String teacherId, String name, String description) {
        if (name == null || !StringUtils.hasText(name))
            name = "Untitled Course";
        if (description == null || !StringUtils.hasText(description))
            description = "Newly created course!";
        return courseRepo.save(new Course(teacherId, name, description)).getId();
    }

    public Course getCourse(String id) {
        return courseRepo.findById(id).orElseThrow();
    }

    public void archiveCourse(String courseId) {
        Course course = getCourse(courseId);
        if (course.isArchived())
            throw new InputValidationException("Course is already archived.");
        course.setArchived(true);
        courseRepo.save(course);
    }

    public void updateCourse(String courseId, String newName, String newDescription) {
        Course course = getCourse(courseId);
        if (course.isArchived())
            throw new InputValidationException("Course is archived.");
        if (newName != null)
            course.setName(StringUtils.hasText(newName) ? newName : "Untitled");
        if (newDescription != null)
            course.setDescription(StringUtils.hasText(newDescription) ? newDescription : "No description.");
        courseRepo.save(course);
    }
}
