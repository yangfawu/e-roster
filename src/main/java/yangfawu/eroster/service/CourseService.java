package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.Invitable;
import yangfawu.eroster.model.User;
import yangfawu.eroster.repository.CourseRepository;
import yangfawu.eroster.util.ServiceUtil;

import java.util.HashSet;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class CourseService {

    private final CourseRepository courseRepo;
    private final UserService userSvc;

    @Autowired
    public CourseService(
            CourseRepository courseRepo,
            UserService userSvc) {
        this.courseRepo = courseRepo;
        this.userSvc = userSvc;
    }

    /**
     * Creates a course with the user as the teacher.
     * @param teacherId the id of the user
     * @param courseName the name of the course
     * @param courseDescription the description of the course
     * @return the generated course
     */
    public Course createCourse(String teacherId, String courseName, String courseDescription) {
        User teacher = userSvc.retrieveUser(teacherId);
        if (teacher == null)
            return null;

        final String COURSE_ID = courseRepo.save(new Course(
            teacher.getId(),
            ServiceUtil.cleanOrDefault(courseName, "Untitled Course"),
            ServiceUtil.cleanOrDefault(courseDescription, "No description.")
        )).getId();

        teacher.getCourses().put(COURSE_ID, User.Role.TEACHER);
        userSvc.updateUser(teacher);

        return courseRepo.getCourseById(COURSE_ID);
    }

    /**
     * Retrieves course by id.
     * @param id the id of course
     * @return the course if it exists
     */
    public Course retrieveCourse(String id) {
        if (id == null)
            return null;
        return courseRepo.getCourseById(StringUtils.trimWhitespace(id));
    }

    /**
     * Archive course by ID.
     * @param id the id of the course
     * @return whether operation was done or not
     */
    public boolean archiveCourse(String id) {
        Course course = retrieveCourse(id);
        if (course == null || course.isArchived())
            return false;

        course.setArchived(true);
        courseRepo.save(course);
        return true;
    }

    /**
     * Helper method to update course info.
     * NOTE: field will be left unchanged if null is passed.
     * @param id the id of the course
     * @param newName the new name of course
     * @param newDescription the new description of course
     * @return whether operation was done or not
     */
    public boolean updateCourseInfo(String id, String newName, String newDescription) {
        Course course = retrieveCourse(id);
        if (course == null || course.isArchived())
            return false;

        if (newName != null)
            course.setName(StringUtils.trimWhitespace(newName));
        if (newDescription != null)
            course.setDescription(StringUtils.trimWhitespace(newDescription));

        courseRepo.save(course);
        return true;
    }

    private static class NoStudentHelperData {

        @NonNull
        public String STUDENT_ID, COURSE_ID;

        @NonNull
        public User student;

        @NonNull
        public Course course;

        public NoStudentHelperData(@NonNull User student, @NonNull Course course) {
            this.student = student;
            this.course = course;
            this.STUDENT_ID = student.getId();
            this.COURSE_ID = course.getId();
        }
    }

    private boolean noStudentHelper(
            String studentId,
            String courseId,
            Function<NoStudentHelperData, Boolean> mainFunc,
            Function<NoStudentHelperData, Boolean> returnFunc) {
        User student = userSvc.retrieveUser(studentId);
        if (student == null)
            return false;

        Course course = retrieveCourse(courseId);
        if (course == null)
            return false;

        final NoStudentHelperData DATA = new NoStudentHelperData(student, course);
        if (course.isArchived() ||
            course.getTeacher().equals(DATA.STUDENT_ID) ||
            course.getStudents().contains(DATA.STUDENT_ID))
            return false;

        if (!mainFunc.apply(DATA))
            return false;

        courseRepo.save(course);
        userSvc.updateUser(student);

        return returnFunc.apply(DATA);
    }

    private boolean noStudentHelper(
            String studentId,
            String courseId,
            Consumer<NoStudentHelperData> mainFunc,
            Function<NoStudentHelperData, Boolean> returnFunc) {
        return noStudentHelper(
            studentId,
            courseId,
            DATA -> {
                mainFunc.accept(DATA);
                return true;
            },
            returnFunc
        );
    }

    private boolean noStudentHelper(
            String studentId,
            String courseId,
            Function<NoStudentHelperData, Boolean> mainFunc) {
        return noStudentHelper(studentId, courseId, mainFunc, arg -> true);
    }

    /**
     * Adds a student to the course and removes all related requests/invites
     * @param userId the id of the student
     * @param courseId the id of the course
     * @return whether the operation was done or not
     */
    public boolean addStudent(String userId, String courseId) {
        return noStudentHelper(
            userId,
            courseId,
            DATA -> {
                DATA.course.getStudents().add(DATA.STUDENT_ID);
                DATA.student.getCourses().put(DATA.COURSE_ID, User.Role.STUDENT);
            },
            DATA -> cancelEntry(DATA.STUDENT_ID, DATA.COURSE_ID)
        );
    }

    /**
     * Helper method to log request/invite
     * @param userId the id of the user
     * @param courseId the id of the course
     * @param first the list to check for user-course link
     * @param second the list to add the user-course link
     * @return whether the operation was done or not
     */
    private boolean createEntry(
            String userId,
            String courseId,
            Function<Invitable, HashSet<String>> first,
            Function<Invitable, HashSet<String>> second) {
        return noStudentHelper(
            userId,
            courseId,
            DATA -> {
                if (first.apply(DATA.student).contains(DATA.COURSE_ID) ||
                    first.apply(DATA.course).contains(DATA.STUDENT_ID)) {
                    addStudent(DATA.STUDENT_ID, DATA.COURSE_ID);
                    return false;
                }
                second.apply(DATA.student).add(DATA.COURSE_ID);
                second.apply(DATA.course).add(DATA.STUDENT_ID);
                return true;
            }
        );
    }

    /**
     * Adds a user request to join the course
     * @param userId the id of the user
     * @param courseId the id of the course
     * @return whether the operation was done or not
     */
    public boolean requestEntry(String userId, String courseId) {
        return createEntry(userId, courseId, Invitable::getInvites, Invitable::getRequests);
    }

    /**
     * Invite a user to join the course
     * @param userId the id of the user
     * @param courseId the id of the course
     * @return whether the operation was done or not
     */
    public boolean offerEntry(String userId, String courseId) {
        return createEntry(userId, courseId, Invitable::getRequests, Invitable::getInvites);
    }

    /**
     * Removes a user's request/invitation to join the course
     * NOTE: you can't be requesting to join AND invited to join at the same time.
     * @param studentId the id of the user
     * @param courseId the id of the course
     * @return whether the operation was done or not
     */
    public boolean cancelEntry(String studentId, String courseId) {
        User student = userSvc.retrieveUser(studentId);
        if (student == null)
            return false;

        Course course = retrieveCourse(courseId);
        if (course == null)
            return false;

        final String COURSE_ID = course.getId();
        student.getRequests().remove(COURSE_ID);
        student.getInvites().remove(COURSE_ID);
        userSvc.updateUser(student);

        final String STUDENT_ID = student.getId();
        course.getRequests().remove(STUDENT_ID);
        course.getInvites().remove(STUDENT_ID);
        courseRepo.save(course);

        return true;
    }

    protected boolean updateCourse(Course course) {
        if (course == null)
            return false;
        courseRepo.save(course);
        return true;
    }

}
