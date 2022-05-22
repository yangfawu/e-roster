package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.DataConflictException;
import yangfawu.eroster.exception.InvalidInputException;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.repository.AttendanceRepository;
import yangfawu.eroster.repository.CourseRepository;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepo;
    private final CourseService courseSvc;
    private final CourseRepository courseRepo;

    @Autowired
    public AttendanceService(
            AttendanceRepository attendanceRepo,
            CourseService courseSvc, CourseRepository courseRepo) {
        this.attendanceRepo = attendanceRepo;
        this.courseSvc = courseSvc;
        this.courseRepo = courseRepo;
    }

    /**
     * Creates a new attendance record for the course
     * @param courseId the id of the course
     * @param timeCreatedFor the time the record is made for
     * @return the newly created attendance
     */
    public Attendance createAttendance(String courseId, LocalDateTime timeCreatedFor) {
        Course course = courseSvc.retrieveCourse(courseId);
        assert course != null;

        if (course.isArchived())
            throw new DataConflictException("Cannot create attendance record for archived course.");

        Attendance doc = new Attendance(courseId);
        if (timeCreatedFor != null)
            doc.setTimeCreatedFor(timeCreatedFor);

        HashMap<String, Attendance.Status> tracker = doc.getMarks();
        for (String studentId : course.getStudents())
            tracker.put(studentId, Attendance.Status.N_A);

        String docId = attendanceRepo.save(doc).getId();
        course.getAttendances().add(docId);
        courseRepo.save(course);

        return attendanceRepo.getAttendanceById(docId);
    }

    /**
     * Attempts to retrieve attendance by ID.
     * @param id the id of the attendance
     * @return the retrieved attendance
     */
    public Attendance retrieveAttendance(String id) {
        if (!StringUtils.hasText(id))
            throw new InvalidInputException("Attendance ID is invalid.");

        id = StringUtils.trimWhitespace(id);

        Attendance doc = attendanceRepo.getAttendanceById(id);
        if (doc == null)
            throw new DataConflictException("Attendance record does not exist.");

        return doc;
    }

    /**
     * Archive an attendance by id
     * @param id the id of the attendance
     */
    public void archiveAttendance(String id) {
        Attendance doc = retrieveAttendance(id);
        assert doc != null;

        if (doc.isArchived())
            throw new DataConflictException("Attendance is already archived.");

        doc.setArchived(true);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
    }

    public void updateTimeCreatedFor(String id, LocalDateTime newTimeCreatedFor) {
        if (newTimeCreatedFor == null)
            throw new InvalidInputException("New time is invalid");

        Attendance doc = retrieveAttendance(id);
        assert doc != null;

        if (doc.isArchived())
            throw new DataConflictException("Cannot modify archived attendance.");

        doc.setTimeCreated(newTimeCreatedFor);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
    }

    /**
     * Updates a student's status on the attendance.
     * @param attendanceId the id of the attendance
     * @param studentId the id of the student
     * @param newStatus the new status of the student
     */
    public void updateStudentStatus(String attendanceId, String studentId, String newStatus) {
        if (!StringUtils.hasText(studentId))
            throw new InvalidInputException("User ID is invalid.");
        if (!StringUtils.hasText(newStatus))
            throw new InvalidInputException("Status is invalid.");

        Attendance doc = retrieveAttendance(attendanceId);
        assert doc != null;

        if (doc.isArchived())
            throw new DataConflictException("Cannot modify archived attendance.");

        Attendance.Status status;
        try {
            status = Attendance.Status.valueOf(newStatus);
        } catch (IllegalArgumentException e) {
            throw new DataConflictException("Status is not recognized");
        }

        studentId = StringUtils.trimWhitespace(studentId);
        if (!doc.getMarks().containsKey(studentId))
            throw new DataConflictException("User is not a student on the attendance.");

        doc.getMarks().put(studentId, status);
        doc.setLastUpdated(LocalDateTime.now());
        attendanceRepo.save(doc);
    }

}
