package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.payload.request.CourseInfoRequest;
import yangfawu.eroster.payload.response.DetailedCourseResponse;
import yangfawu.eroster.payload.response.SimpleCourseResponse;
import yangfawu.eroster.payload.response.SimpleUserResponse;
import yangfawu.eroster.service.CourseService;
import yangfawu.eroster.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/private/course")
@Log4j2
public class CourseController {

    private final UserService userSvc;
    private final CourseService courseSvc;

    public CourseController(
            UserService userSvc,
            CourseService courseSvc) {
        this.userSvc = userSvc;
        this.courseSvc = courseSvc;
    }

    @PostMapping("/create")
    public String createCourse(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestBody CourseInfoRequest req) {
        final String USER_ID = cred.getPublicId();
        PublicUser user = userSvc.getPublicUser(USER_ID);
        if (user.getRole() != PublicUser.Role.TEACHER)
            throw new ForbiddenException("User needs to be a teacher to create course.");
        return courseSvc.createCourse(
                USER_ID,
                req.getName(),
                req.getDescription()
        );
    }

    private SimpleCourseResponse getSimpleCourse(String courseId) {
        Course course = courseSvc.getCourse(courseId);
        return SimpleCourseResponse.from(
                course,
                userSvc.getPublicUser(course.getTeacherId())
        );
    }

    @GetMapping("/{courseId}")
    public SimpleCourseResponse getCourse(@PathVariable String courseId) {
        return getSimpleCourse(courseId);
    }

    @GetMapping("/detailed/{courseId}")
    public DetailedCourseResponse getDetailedCourse(@PathVariable String courseId) {
        Course course = courseSvc.getCourse(courseId);
        List<SimpleUserResponse> students = course.getStudentIds()
                .stream()
                .map(userSvc::getPublicUser)
                .map(SimpleUserResponse::from)
                .collect(Collectors.toList());
        SimpleCourseResponse base = getSimpleCourse(courseId);
        return DetailedCourseResponse.builder()
                .id(base.getId())
                .name(base.getName())
                .description(course.getDescription())
                .teacher(base.getTeacher())
                .created(course.getCreated())
                .archived(course.isArchived())
                .students(students)
                .build();
    }

    private List<SimpleCourseResponse> getTeacherCourses(String userId) {
        // TODO
        return List.of();
    }

    private List<SimpleCourseResponse> getStudentCourses(String userId) {
        // TODO
        return List.of();
    }

    @GetMapping("")
    public Object getCourses(@AuthenticationPrincipal PrivateUser cred) {
        PublicUser user = userSvc.getPublicUser(cred.getPublicId());
        final String USER_ID = user.getId();
        if (user.getRole() == PublicUser.Role.TEACHER)
            return getTeacherCourses(USER_ID);
        return getStudentCourses(USER_ID);
    }

    private void checkForAccess(PrivateUser cred, String courseId) {
        PublicUser user = userSvc.getPublicUser(cred.getPublicId());
        if (user.getRole() != PublicUser.Role.TEACHER)
            throw new ForbiddenException("User is not a teacher.");

        Course course = courseSvc.getCourse(courseId);
        if (!course.getTeacherId().equals(user.getId()))
            throw new ForbiddenException("User does not teach the course.");
    }

    @PostMapping("/update/{courseId}")
    public void updateCourseInfo(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestBody CourseInfoRequest req) {
        checkForAccess(cred, courseId);
        courseSvc.updateCourse(
                courseId,
                req.getName(),
                req.getDescription()
        );
    }

    @PostMapping("/archive/{courseId}")
    public void archiveCourse(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId) {
        checkForAccess(cred, courseId);
        courseSvc.archiveCourse(courseId);
    }

}
