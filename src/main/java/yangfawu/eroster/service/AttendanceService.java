package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.repository.AttendanceRepository;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final CourseService courseSvc;

    @Autowired
    public AttendanceService(
            AttendanceRepository attendanceRepo,
            CourseService courseSvc) {
        this.attendanceRepo = attendanceRepo;
        this.courseSvc = courseSvc;
    }

    /**
     * Creates a new attendance record for the course
     * @param courseId the id of the course
     * @param msTimeCreatedFor the time the record is made for
     * @return the newly created attendance
     */
    public Attendance createAttendance(String courseId, long msTimeCreatedFor) {
        Course course = courseSvc.retrieveCourse(courseId);
        if (course == null || course.isArchived())
            return null;

        Attendance doc = new Attendance(course.getId());
        doc.setMsTimeCreatedFor(msTimeCreatedFor);

        HashMap<String, Attendance.Status> tracker = doc.getMarks();
        for (String studentId : course.getStudents())
            tracker.put(studentId, Attendance.Status.N_A);

        final String DOC_ID = attendanceRepo.save(doc).getId();
        course.getAttendances().add(DOC_ID);
        courseSvc.updateCourse(course);

        return attendanceRepo.getAttendanceById(DOC_ID);
    }

    /**
     * Attempts to retrieve attendance by ID.
     * @param id the id of the attendance
     * @return the retrieved attendance
     */
    public Attendance retrieveAttendance(String id) {
        if (id == null)
            return null;
        return attendanceRepo.getAttendanceById(StringUtils.trimWhitespace(id));
    }

    private Attendance retrieveAttendanceIfNotArchived(String id) {
        Attendance doc = retrieveAttendance(id);
        if (doc == null || doc.isArchived())
            return null;
        return doc;
    }

    /**
     * Archive an attendance by id
     * @param id the id of the attendance
     * @returns whether the operation was done or not
     */
    public boolean archiveAttendance(String id) {
        Attendance doc = retrieveAttendanceIfNotArchived(id);
        if (doc == null)
            return false;

        doc.setArchived(true);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
        return true;
    }

    /**
     * Updates the time the record was created to keep track of.
     * @param id id of the attendance
     * @param newMsTimeCreatedFor new created time
     * @return whether the operation was done or not
     */
    public boolean updateTimeCreatedFor(String id, long newMsTimeCreatedFor) {
        Attendance doc = retrieveAttendanceIfNotArchived(id);
        if (doc == null)
            return false;

        doc.setMsTimeCreatedFor(newMsTimeCreatedFor);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
        return true;
    }

    /**
     * Updates a student's status on the attendance.
     * @param attendanceId the id of the attendance
     * @param studentId the id of the student
     * @param newStatus the new status of the student
     */
    public boolean updateStudentStatus(String attendanceId, String studentId, String newStatus) {
        Attendance doc = retrieveAttendanceIfNotArchived(attendanceId);
        if (doc == null)
            return false;

        Attendance.Status status;
        try {
            status = Attendance.Status.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            return false;
        }

        final String STUDENT_ID = StringUtils.trimWhitespace(studentId);
        if (!doc.getMarks().containsKey(STUDENT_ID))
            return false;

        doc.getMarks().put(STUDENT_ID, status);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
        return true;
    }

}
