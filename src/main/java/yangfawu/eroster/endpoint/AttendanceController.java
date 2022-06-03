package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.payload.response.PageResponse;
import yangfawu.eroster.payload.response.SimpleAttendanceResponse;
import yangfawu.eroster.service.AttendanceService;
import yangfawu.eroster.service.CourseService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/private/attendance")
@Log4j2
public class AttendanceController {

    private final CourseService courseSvc;
    private final AttendanceService formSvc;

    @Autowired
    public AttendanceController(
            CourseService courseSvc,
            AttendanceService formSvc) {
        this.courseSvc = courseSvc;
        this.formSvc = formSvc;
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
        return formSvc.startForm(verifyCred(cred, courseId).getId());
    }

    @GetMapping("/{attendanceId}")
    public Attendance getForm(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String attendanceId) {
        Attendance form = formSvc.getForm(attendanceId);
        verifyCred(cred, form.getCourseId());
        return form;
    }

    @GetMapping("/course/{courseId}")
    public PageResponse<Attendance> getForms(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Attendance> result = formSvc.getCourseForms(
                verifyCred(cred, courseId).getId(),
                page,
                size
        );
        return PageResponse.from(result, result.getContent());
    }

    @PostMapping("/mark/{attendanceId}/{studentId}/{mark}")
    public void mark(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String attendanceId,
            @PathVariable String studentId,
            @PathVariable Attendance.Mark mark) {
        Attendance form = formSvc.getForm(attendanceId);
        Course course = verifyCred(cred, form.getCourseId());
        if (!course.getStudentIds().contains(studentId))
            throw new InputValidationException("Student is not in the course.");
        formSvc.markStudent(form.getId(), studentId, mark);
    }

    @GetMapping("/status/{courseId}")
    public PageResponse<SimpleAttendanceResponse> getStatus(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Course course = courseSvc.getCourse(courseId);
        final String USER_ID = cred.getPublicId();
        if (!course.getStudentIds().contains(USER_ID))
            throw new ForbiddenException("User is not in the course.");

        Page<Attendance> result = formSvc.getCourseMarks(course.getId(), USER_ID, page, size);
        List<SimpleAttendanceResponse> marks = result.getContent()
                .stream()
                .map(f -> new SimpleAttendanceResponse(
                        f.getCreated(),
                        f.getMarks().get(USER_ID)
                ))
                .collect(Collectors.toList());
        return PageResponse.from(result, marks);
    }

}
