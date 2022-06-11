package yangfawu.eroster.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Attendance;
import yangfawu.eroster.model.Attendance.AttendanceMark;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.ListReferenceItem;
import yangfawu.eroster.repository.AttendanceRepository;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private final AttendanceRepository attRepo;
    private final UserService userSvc;
    private final CourseService courseSvc;
    private final AttendancesCollectionService attColSvc;
    private final StudentsCollectionService studColSvc;

    public Attendance getAttendanceById(String id) {
        return Optional.ofNullable(attRepo.find(id))
                .orElseThrow(() -> new NoSuchElementException("Can't find attendance record."));
    }

    public List<ListReferenceItem> getAttendanceIds(Jwt token, String courseId, int start, int size) {
        courseSvc.getCourseIfTokenIsInGroup(token, courseId);
        return attColSvc.getItems(
                courseId,
                start,
                size
        );
    }

    public Attendance getAttendanceIfTokenIsTeacher(Jwt token, String attendanceId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Attendance doc = getAttendanceById(attendanceId);
        if (!doc.getAuthorId().equals(UID))
            throw new ForbiddenException("User is not the teacher of the course.");
        return doc;
    }

    public Attendance getAttendanceInfo(Jwt token, String attendanceId) {
        return getAttendanceIfTokenIsTeacher(token, attendanceId);
    }

    public Attendance getBasicAttendanceInfo(Jwt token, String attendanceId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Attendance doc = getAttendanceById(attendanceId);

        do {
            if (doc.getAuthorId().equals(UID))
                break;
            if (studColSvc.hasItem(doc.getCourseId(), UID))
                break;
            throw new ForbiddenException("User has to be the teacher or a student of the course.");
        } while (false);

        return Attendance.builder()
                .id(attendanceId)
                .finalized(doc.isFinalized())
                .created(doc.getCreated())
                .build();
    }

    public Attendance getMark(Jwt token, String attendanceId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Attendance doc = getAttendanceById(attendanceId);
        if (!studColSvc.hasItem(doc.getCourseId(), UID))
            throw new ForbiddenException("User is not part of the course.");

        HashMap<String, AttendanceMark> marks = new HashMap<>();
        marks.put(UID, doc.getMarks().get(UID));
        return Attendance.builder()
                .id(attendanceId)
                .finalized(doc.isFinalized())
                .created(doc.getCreated())
                .marks(marks)
                .build();
    }

    public Attendance createDoc(Jwt token, String courseId) {
        Course course = courseSvc.getCourseIfTokenIsTeacher(token, courseId);
        if (course.isArchived())
            throw new InputValidationException("Cannot start new attendance for archived course.");

        HashMap<String, AttendanceMark> marks = new HashMap<>();
        int N = studColSvc.getCount(courseId);
        int i = 0;
        while (i < N)
            for (ListReferenceItem item : studColSvc.getItems(courseId, i, 20)) {
                marks.put(item.getRef(), AttendanceMark.NA);
                i++;
            }

        final String AID = attRepo.newId();
        Attendance doc = Attendance.builder()
                .id(AID)
                .courseId(courseId)
                .authorId(course.getTeacherId())
                .finalized(false)
                .created(Instant.now())
                .updated(Instant.now())
                .marks(marks)
                .build();
        attColSvc.addReference(courseId, AID);
        attRepo.create(AID, doc);

        return doc;
    }

    public void finalize(Jwt token, String attendanceId) {
        Attendance doc = getAttendanceIfTokenIsTeacher(token, attendanceId);
        if (doc.isFinalized())
            throw new InputValidationException("Attendance record is already finalized.");

        doc.setFinalized(true);
        doc.setUpdated(Instant.now());
        attRepo.update(attendanceId, doc, "finalized", "updated");
    }

    public void markStudent(Jwt token, String studentId, AttendanceMark mark, String attendanceId) {
        Attendance doc = getAttendanceIfTokenIsTeacher(token, attendanceId);
        if (doc.isFinalized())
            throw new InputValidationException("Cannot edit attendance because it is finalized.");

        if (!studColSvc.hasItem(doc.getCourseId(), studentId))
            throw new InputValidationException("Student is not in the course.");

        doc.getMarks().put(studentId, mark);
        doc.setUpdated(Instant.now());
        attRepo.update(attendanceId, doc, "marks", "updated");
    }
}
