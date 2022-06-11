package yangfawu.eroster.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.payload.request.CourseCreateRequest;
import yangfawu.eroster.payload.request.CourseUpdateRequest;
import yangfawu.eroster.service.CourseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/course")
public class CourseController {

    private final CourseService courseSvc;

    @GetMapping("")
    public List<ListReferenceItem> getCourseIds(
            @AuthenticationPrincipal Jwt token,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {
        return courseSvc.getCourseIds(token, start, size);
    }

    @GetMapping("/students/{courseId}")
    public List<ListReferenceItem> getStudentIds(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {
        return courseSvc.getStudentIds(token, courseId, start, size);
    }

    @GetMapping("/{courseId}")
    public Course getCourse(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "false") boolean detailed) {
        if (detailed)
            return courseSvc.getCourseInfo(token, courseId);
        return courseSvc.getBasicCourseInfo(courseId);
    }

    @PostMapping("/create")
    public Course createCourse(
            @AuthenticationPrincipal Jwt token,
            @RequestBody CourseCreateRequest req) {
        req.validate();
        return courseSvc.createCourse(token, req);
    }

    @PostMapping("/archive/{courseId}")
    public void archiveCourse(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId) {
        courseSvc.archiveCourse(token, courseId);
    }

    @PostMapping("/update")
    public Course updateCourse(
            @AuthenticationPrincipal Jwt token,
            @RequestBody CourseUpdateRequest req) {
        req.validate();
        return courseSvc.updateCourse(token, req);
    }

}
