package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.service.CourseService;

@RestController
@RequestMapping("/api/private/attendance")
@Log4j2
public class AttendanceController {

    private final CourseService courseSvc;

    public AttendanceController(CourseService courseSvc) {
        this.courseSvc = courseSvc;
    }

    private Course verifyCred(PrivateUser cred, String courseId) {
        Course course = courseSvc.getCourse(courseId);
        if (!course.getTeacherId().equals(cred.getPublicId()))
            throw new ForbiddenException("User does not teach the course.");
        return course;
    }

    @PostMapping("/start/{courseId}")
    public String startForm(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId) {
        return null;
    }

    @GetMapping("/{attendanceId}")
    public Object getForm(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String attendanceId) {
        return null;
    }

    @GetMapping("/course/{courseId}")
    public Object getForms(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return null;
    }

    @PostMapping("/mark/{attendanceId}/{studentId}/{mark}")
    public void mark(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String attendanceId,
            @PathVariable String studentId,
            @PathVariable String mark) {

    }

    @GetMapping("/status/{courseId}")
    public Object getStatus(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return null;
    }

}
