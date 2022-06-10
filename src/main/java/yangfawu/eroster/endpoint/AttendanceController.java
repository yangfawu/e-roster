package yangfawu.eroster.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import yangfawu.eroster.model.Attendance.AttendanceMark;

@RestController
@RequestMapping("/api/private/attendance")
public class AttendanceController {

    public Object getAttendanceIds(String courseId, int start, int size) {
        // get course by ID
        // make sure user ID == teacherId
        // fetch page of attendance IDs
        return null;
    }

    public Object getReducedAttendance(String id) {
        // get attendance by ID
        // fetch simple info [id, finalized, created]
        return null;
    }

    public Object getAttendance(String id) {
        // get attendance by ID
        // make sure user ID == authorID
        return null;
    }

    public Object createAttendance(String courseId) {
        // get course by ID
        // make sure user ID == teacher ID
        // make sure course is not already archived
        // create attendance record
        // add attendance reference to course
        return null;
    }

    public void finalizeAttendance(String id) {
        // get attendance by ID
        // make sure user ID == author ID
        // finalize document
    }

    public void markStudent(String id, String studentId, AttendanceMark mark) {
        // fetch attendance by id
        // make sure user ID == author ID
        // make sure doc is not finalized
        // make sure studentId is in doc
        // mark the doc
    }

}
