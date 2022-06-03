package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.payload.request.CourseInfoRequest;
import yangfawu.eroster.payload.response.*;
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

    @GetMapping("/{courseId}")
    public ICourseResponse getCourse(
            @PathVariable String courseId,
            @RequestParam(defaultValue = "false") boolean detailed) {
        Course course = courseSvc.getCourse(courseId);
        SimpleCourseResponse base = SimpleCourseResponse.from(
                course,
                userSvc.getPublicUser(course.getTeacherId())
        );
        if (!detailed)
            return base;

        List<SimpleUserResponse> students = course.getStudentIds()
                .stream()
                .map(userSvc::getPublicUser)
                .map(SimpleUserResponse::from)
                .collect(Collectors.toList());
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

    @GetMapping("/list")
    public PageResponse<SimpleCourseResponse> getCourses(
            @AuthenticationPrincipal PrivateUser cred,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PublicUser user = userSvc.getPublicUser(cred.getPublicId());

        final String USER_ID = user.getId();
        Page<Course> result = user.getRole() == PublicUser.Role.TEACHER ?
                courseSvc.getTeacherCourses(USER_ID, page, size) :
                courseSvc.getStudentCourses(USER_ID, page, size);

        List<SimpleCourseResponse> courses = result.getContent()
                .stream()
                .map(course -> SimpleCourseResponse.from(
                        course,
                        userSvc.getPublicUser(course.getTeacherId())
                ))
                .collect(Collectors.toList());
        return PageResponse.from(result, courses);
    }

    @PostMapping("/update/{courseId}")
    public void updateCourseInfo(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestBody CourseInfoRequest req) {
        Course course = courseSvc.getCourse(courseId);
        if (!course.getTeacherId().equals(cred.getPublicId()))
            throw new ForbiddenException("User does not teach the course.");
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
        Course course = courseSvc.getCourse(courseId);
        if (!course.getTeacherId().equals(cred.getPublicId()))
            throw new ForbiddenException("User does not teach the course.");
        courseSvc.archiveCourse(courseId);
    }

}
