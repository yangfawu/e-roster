package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.CourseInvitation;
import yangfawu.eroster.model.PublicUser;
import yangfawu.eroster.model.StudentRequest;
import yangfawu.eroster.repository.CourseInvitationRepository;
import yangfawu.eroster.repository.StudentRequestRepository;

import java.util.function.BiConsumer;

public class ConnectionService {

    private final CourseInvitationRepository courseInvRepo;
    private final StudentRequestRepository studentReqRepo;
    private final UserService userSvc;
    private final CourseService courseSvc;

    @Autowired
    public ConnectionService(
            CourseInvitationRepository courseInvRepo,
            StudentRequestRepository studentReqRepo,
            UserService userSvc,
            CourseService courseSvc) {
        this.courseInvRepo = courseInvRepo;
        this.studentReqRepo = studentReqRepo;
        this.userSvc = userSvc;
        this.courseSvc = courseSvc;
    }

    public boolean hasRequest(String userId, String courseId) {
        return studentReqRepo.existsByCourseIdAndAndStudentId(courseId, userId);
    }

    public boolean hasInvitation(String userId, String courseId) {
        return courseInvRepo.existsByCourseIdAndAndInviteeId(courseId, userId);
    }

    private void entryHelper(String userId, String courseId, BiConsumer<String, String> consumer) {
        PublicUser user = userSvc.getPublicUser(userId);
        if (user.getRole() != PublicUser.Role.STUDENT)
            throw new InputValidationException("User is not a student");
        final String USER_ID = user.getId();

        Course course = courseSvc.getCourse(courseId);
        if (course.isArchived())
            throw new InputValidationException("Cannot join archived courses.");
        final String COURSE_ID = course.getId();

        if (course.getStudentIds().contains(USER_ID))
            throw new InputValidationException("User is already in the course.");

        consumer.accept(USER_ID, COURSE_ID);
    }

    public void requestEntry(String userId, String courseId) {
        entryHelper(userId, courseId, (USER_ID, COURSE_ID) -> {
            if (hasRequest(USER_ID, COURSE_ID))
                throw new InputValidationException("User has already requested entry.");

            if (hasInvitation(USER_ID, COURSE_ID)) {
                courseInvRepo.deleteByCourseIdAndInviteeId(COURSE_ID, USER_ID);
                courseSvc.addStudent(COURSE_ID, USER_ID);
                return;
            }

            studentReqRepo.save(new StudentRequest(USER_ID, COURSE_ID));
        });
    }

    public void inviteStudent(String userId, String courseId) {
        entryHelper(userId, courseId, (USER_ID, COURSE_ID) -> {
            if (hasInvitation(USER_ID, COURSE_ID))
                throw new InputValidationException("User has already been invited to this course.");

            if (hasRequest(USER_ID, COURSE_ID)) {
                studentReqRepo.deleteByCourseIdAndStudentId(COURSE_ID, USER_ID);
                courseSvc.addStudent(COURSE_ID, USER_ID);
                return;
            }

            courseInvRepo.save(new CourseInvitation(USER_ID, COURSE_ID));
        });
    }

}
