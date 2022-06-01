package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PrivateUser;
import yangfawu.eroster.service.ConnectionService;
import yangfawu.eroster.service.CourseService;

@RestController
@RequestMapping("/api/private/connection")
@Log4j2
public class ConnectionController {

    private final ConnectionService conSvc;
    private final CourseService courseSvc;

    @Autowired
    public ConnectionController(
            ConnectionService conSvc,
            CourseService courseSvc) {
        this.conSvc = conSvc;
        this.courseSvc = courseSvc;
    }

    @PostMapping("/request/{courseId}")
    public void submitRequest(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String courseId) {
        conSvc.requestEntry(cred.getPublicId(), courseId);
    }

    @PostMapping("/invite/{userId}/to/{courseId}")
    public void submitInvitation(
            @AuthenticationPrincipal PrivateUser cred,
            @PathVariable String userId,
            @PathVariable String courseId) {
        Course course = courseSvc.getCourse(courseId);
        if (!course.getTeacherId().equals(cred.getPublicId()))
            throw new ForbiddenException("User does not teach the course.");
        conSvc.inviteStudent(userId, course.getId());
    }

}
