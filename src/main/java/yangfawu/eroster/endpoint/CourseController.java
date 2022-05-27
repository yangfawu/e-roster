package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.User;
import yangfawu.eroster.model.UserCredential;
import yangfawu.eroster.service.CourseService;
import yangfawu.eroster.service.UserService;

@RestController
@RequestMapping("/api/private/course")
@Log4j2
public class CourseController {

    private final CourseService courseSvc;
    private final UserService userSvc;

    @Autowired
    public CourseController(
            CourseService courseSvc,
            UserService userSvc) {
        this.courseSvc = courseSvc;
        this.userSvc = userSvc;
    }

    /**
     * Creates a new course with the user as the teacher.
     * @param courseInfo the info of the new course
     * @return the newly created course
     */
    @PostMapping("/create")
    public Course createCourse(
            @RequestBody Course courseInfo,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        Course course = courseSvc.createCourse(
            cred.getId(),
            courseInfo.getName(),
            courseInfo.getDescription()
        );
        assert course != null;
        return course;
    }

    /**
     * Retrieves info of course by ID.
     * NOTE: some course information will be hidden if client is not the teacher
     * @param id the id of the course
     * @return the info of course
     */
    @GetMapping("/{id}")
    public Course getCourse(
            @PathVariable String id,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        Course course = courseSvc.retrieveCourse(id);
        assert course != null;
        if (!course.getTeacher().equals(cred.getId())) {
            course.setAttendances(null);
            course.setInvites(null);
            course.setRequests(null);
            course.setStudents(null);
        }
        return course;
    }

    /**
     * Updates a course's name or description by ID.
     * NOTE: client must be the teacher
     * @param newInfo the new course info along with its ID
     */
    @PostMapping("/update")
    public void updateCourseInfo(
            @RequestBody Course newInfo,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        Course course = courseSvc.retrieveCourse(newInfo.getId());
        assert course != null;

        if (!course.getTeacher().equals(cred.getId()))
            throw new RuntimeException("User is not the teacher of the course.");

        courseSvc.updateCourseName(course.getId(), newInfo.getName());
        courseSvc.updateCourseDescription(course.getId(), newInfo.getDescription());
    }

    /**
     * User requests entry to a course by ID.
     * @param courseId the ID of the course
     */
    @PostMapping("/request/{courseId}")
    public void studentRequestEntry(
            @PathVariable String courseId,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        Course course = courseSvc.retrieveCourse(courseId);
        assert course != null;

        courseSvc.requestEntry(cred.getId(), course.getId());
    }

    /**
     * Creates an invitation for a student to the course.
     * NOTE: User must be the teacher of the course.
     * @param studentId the ID of the potential student
     * @param courseId the ID of the course
     */
    @PostMapping("/invite/{studentId}/to/{courseId}")
    public void teacherSendInvite(
            @PathVariable String studentId,
            @PathVariable String courseId,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        Course course = courseSvc.retrieveCourse(courseId);
        assert course != null;

        if (!course.getTeacher().equals(cred.getId()))
            throw new RuntimeException("User is not the teacher of the course.");

        courseSvc.offerEntry(studentId, course.getId());
    }

    /**
     * Removes an invitation/request to a course for the specified user.
     * NOTE: only the teacher of the course or the invited user can perform this
     * @param userId the ID of the user
     * @param courseId the ID of the course
     */
    @PostMapping("/cancelEntry/{userId}/to/{courseId}")
    public void cancelEntry(
            @PathVariable String userId,
            @PathVariable String courseId,
            UsernamePasswordAuthenticationToken user) {
        UserCredential cred = (UserCredential) user.getPrincipal();
        User student = userSvc.retrieveUser(userId);
        Course course = courseSvc.retrieveCourse(courseId);
        assert student != null && course != null;

        if (!student.getId().equals(cred.getId()) && !course.getTeacher().equals(cred.getId()))
            throw new RuntimeException("User is neither the student nor the teacher.");

        courseSvc.cancelEntry(userId, courseId);
    }

}
