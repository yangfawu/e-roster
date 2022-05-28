package yangfawu.eroster.endpoint;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.service.AttendanceService;

@RestController
@RequestMapping("/api/private/attendance")
public class AttendanceController {

    private final AttendanceService attendanceSvc;

    public AttendanceController(AttendanceService attendanceSvc) {
        this.attendanceSvc = attendanceSvc;
    }

    /**
     * Starts a new attendance for the provided course.
     * @param info the attendance info containing course ID and time created for
     * @return the newly created attendance
     */
    @PostMapping("/start/{courseId}/for/{time}")
    public Attendance startAttendance(
        @PathVariable String courseId,
        @PathVariable long time,
        UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        // TODO
        return null;
    }

    @GetMapping("/{courseId}")
    public Attendance getAttendance(
            @PathVariable String courseId,
            UsernamePasswordAuthenticationToken user) {
        return null;
    }

}
