package yangfawu.eroster.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.User.UserType;
import yangfawu.eroster.payload.request.CourseCreateRequest;
import yangfawu.eroster.payload.request.CourseUpdateRequest;
import yangfawu.eroster.repository.CourseRepository;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final UserService userSvc;
    private final CourseRepository courseRepo;
    private final CoursesCollectionService courseColSvc;
    private final StudentsCollectionService  studColSvc;

    public List<ListReferenceItem> getCourseIds(Jwt token, int start, int size) {
        return courseColSvc.getItems(
                userSvc.getUserIdFromFirebaseJwt(token),
                start,
                size
        );
    }

    public Course getCourseById(String id) {
        return Optional.ofNullable(courseRepo.find(id))
                .orElseThrow(() -> new NoSuchElementException("Can't find course."));
    }

    public Course getCourseIfTokenIsInGroup(Jwt token, String courseId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Course course = getCourseById(courseId);

        do {
            if (course.getTeacherId().equals(UID))
                break;
            if (studColSvc.hasItem(courseId, UID))
                break;
            throw new ForbiddenException("User must the teacher or a student in the course.");
        } while (false);

        return course;
    }

    public Course getCourseIfTokenIsTeacher(Jwt token, String courseId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Course course = getCourseById(courseId);
        if (!course.getTeacherId().equals(UID))
            throw new ForbiddenException("User is not the teacher of the course.");
        return course;
    }

    public List<ListReferenceItem> getStudentIds(Jwt token, String courseId, int start, int size) {
        return studColSvc.getItems(
                getCourseIfTokenIsInGroup(token, courseId).getId(),
                start,
                size
        );
    }

    public Course getCourseInfo(Jwt token, String courseId) {
        return getCourseIfTokenIsInGroup(token, courseId);
    }

    public Course getBasicCourseInfo(String courseId) {
        Course course = getCourseById(courseId);
        // get basic course info [id, name, teacherId] by ID
        return Course.builder()
                .id(courseId)
                .name(course.getName())
                .teacherId(course.getTeacherId())
                .build();
    }

    public Course createCourse(Jwt token, CourseCreateRequest req) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        User user = userSvc.getUserById(UID);
        if (user.getAccountType() != UserType.TEACHER)
            throw new ForbiddenException("User must be a teacher.");

        final String CID = courseRepo.newId();
        Course course = Course.builder()
                .id(CID)
                .name(req.getName())
                .description(req.getDescription())
                .archived(false)
                .teacherId(UID)
                .created(Instant.now())
                .build();
        courseRepo.create(CID, course);
        courseColSvc.addReference(UID, CID);

        return course;
    }

    public void archiveCourse(Jwt token, String courseId) {
        Course course = getCourseIfTokenIsTeacher(token, courseId);
        if (course.isArchived())
            throw new InputValidationException("Course is already archived.");

        course.setArchived(true);
        courseRepo.update(courseId, course, "archived");
    }

    public Course updateCourse(Jwt token, CourseUpdateRequest req) {
        final String CID = req.getId();
        Course course = getCourseIfTokenIsTeacher(token, CID);
        if (course.isArchived())
            throw new InputValidationException("Cannot edit archived course.");

        if (req.getName() != null)
            course.setName(req.getName());
        if (req.getDescription() != null)
            course.setDescription(req.getDescription());

        courseRepo.update(CID, course, "name", "description");

        return course;
    }
}
