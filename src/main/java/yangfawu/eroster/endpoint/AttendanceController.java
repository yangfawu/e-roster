package yangfawu.eroster.endpoint;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Attendance.AttendanceMark;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.service.AttendanceService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/attendance")
public class AttendanceController {

    private final AttendanceService attSvc;

    @GetMapping("/attendances/{courseId}")
    public List<ListReferenceItem> getAttendanceIds(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int size) {
        return attSvc.getAttendanceIds(token, courseId, start, size);
    }

    @GetMapping("/{attendanceId}")
    public Attendance getAttendance(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String attendanceId,
            @RequestParam(defaultValue = "false") boolean detailed) {
        if (detailed)
            return attSvc.getAttendanceInfo(token, attendanceId);
        return attSvc.getBasicAttendanceInfo(token, attendanceId);
    }

    @GetMapping("/mark/{attendanceId}")
    public Attendance getMark(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String attendanceId) {
        return attSvc.getMark(token, attendanceId);
    }

    @PostMapping("/create/{courseId}")
    public Attendance createAttendance(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String courseId) {
        return attSvc.createDoc(token, courseId);
    }

    @PostMapping("/finalize/{attendanceId}")
    public void finalizeAttendance(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String attendanceId) {
        attSvc.finalize(token, attendanceId);
    }

    @PostMapping("/mark/{studentId}/with/{mark}/in/{attendanceId}")
    public void markStudent(
            @AuthenticationPrincipal Jwt token,
            @PathVariable String studentId,
            @PathVariable AttendanceMark mark,
            @PathVariable String attendanceId) {
        attSvc.markStudent(token, studentId, mark, attendanceId);
    }

}
