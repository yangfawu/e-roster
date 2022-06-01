package yangfawu.eroster.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final UserService userSvc;

    public CourseService(
            CourseRepository courseRepo,
            UserService userSvc) {
        this.courseRepo = courseRepo;
        this.userSvc = userSvc;
    }

    protected boolean courseExistsById(String courseId) {
        return courseRepo.existsById(courseId);
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

    private Pageable generatePageable(int page, int size) {
        if (page < 1)
            throw new InputValidationException("Page numbers are 1-indexed.");
        if (size < 1)
            throw new InputValidationException("Page size has to be at least 1.");
        return PageRequest.of(page - 1, size);
    }

    public Page<Course> getStudentCourses(String studentId, int page, int size) {
        return courseRepo.findByStudentIdsContainingOrderByCreated(
                studentId,
                generatePageable(page, size)
        );
    }

    public Page<Course> getTeacherCourses(String teacherId, int page, int size) {
        return courseRepo.findByTeacherId(
                teacherId,
                generatePageable(page, size)
        );
    }

    public void addStudent(String courseId, String studentId) {
        PublicUser user = userSvc.getPublicUser(studentId);
        if (user.getRole() != PublicUser.Role.STUDENT)
            throw new InputValidationException("User is not a student.");

        Course course = getCourse(courseId);
        if (course.isArchived())
            throw new InputValidationException("User cannot join archived courses");
        if (course.getStudentIds().contains(user.getId()))
            throw new InputValidationException("user is already a student in the course.");

        course.getStudentIds().add(user.getId());
        courseRepo.save(course);
    }

}
