package yangfawu.eroster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import yangfawu.eroster.exception.DataConflictException;
import yangfawu.eroster.exception.InvalidInputException;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.Invitable;
import yangfawu.eroster.model.User;
import yangfawu.eroster.repository.CourseRepository;
import yangfawu.eroster.repository.UserRepository;

import java.util.HashSet;

@Service
public class CourseService {

    public final CourseRepository courseRepo;
    public final UserRepository userRepo;
    public final UserService userSvc;

    @Autowired
    public CourseService(
            CourseRepository courseRepo,
            UserRepository userRepo,
            UserService userSvc) {
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
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
        if (!StringUtils.hasText(courseName))
            throw new InvalidInputException("Course name is invalid.");

        User teacher = userSvc.retrieveUser(teacherId);
        assert teacher != null;

        courseName = StringUtils.trimWhitespace(courseName);
        courseDescription = courseDescription == null ? "" : StringUtils.trimWhitespace(courseDescription);

        Course course = new Course(teacher.getId(), courseName, courseDescription);
        String courseId = courseRepo.save(course).getId();

        teacher.getCourses().put(courseId, User.Role.TEACHER);
        userRepo.save(teacher);

        return courseRepo.getCourseById(courseId);
    }

    /**
     * Retrieves course by id.
     * @param id the id of course
     * @return the course if it exists
     */
    public Course retrieveCourse(String id) {
        if (!StringUtils.hasText(id))
            throw new InvalidInputException("Course ID is invalid.");

        id = StringUtils.trimWhitespace(id);

        Course course = courseRepo.getCourseById(id);
        if (course == null)
            throw new DataConflictException("Course does not exist.");

        return course;
    }

    /**
     * Archive course by ID.
     * @param id the id of the course
     */
    public void archiveCourse(String id) {
        Course course = retrieveCourse(id);
        assert course != null;

        if (course.isArchived())
            throw new DataConflictException("Course is already archived.");

        course.setArchived(true);
        courseRepo.save(course);
    }

    /**
     * Helper method to update course info.
     * NOTE: field will be left unchanged if null is passed.
     * @param id the id of the course
     * @param newName the new name of course
     * @param newDescription the new description of course
     */
    private void updateCourseInfo(String id, String newName, String newDescription) {
        Course course = retrieveCourse(id);
        assert course != null;

        if (course.isArchived())
            throw new DataConflictException("Cannot modify an archived course.");

        course.setName(newName == null ? course.getName() : newName);
        course.setDescription(newDescription == null ? course.getDescription() : newDescription);
        courseRepo.save(course);
    }

    /**
     * Updates the course's name.
     * @param id the id of the course
     * @param newName the new name of the course
     */
    public void updateCourseName(String id, String newName) {
        if (!StringUtils.hasText(newName))
            throw new InvalidInputException("New name is invalid.");

        newName = StringUtils.trimWhitespace(newName);

        updateCourseInfo(id, newName, null);
    }

    /**
     * Updates the course's description
     * @param id the id of the course
     * @param newDescription the new description of the course
     */
    public void updateCourseDescription(String id, String newDescription) {
        if (!StringUtils.hasText(newDescription))
            throw new InvalidInputException("New description is invalid.");

        newDescription = StringUtils.trimWhitespace(newDescription);

        updateCourseInfo(id, null, newDescription);
    }

    /**
     * Adds a student to the course and removes all related requests/invites
     * @param userId the id of the student
     * @param courseId the id of the course
     */
    public void addStudent(String userId, String courseId) {
        User student = userSvc.retrieveUser(userId);
        Course course = retrieveCourse(courseId);
        assert student != null && course != null;

        userId = student.getId();
        courseId = course.getId();

        if (course.isArchived())
            throw new DataConflictException("Cannot add student to archived course.");
        if (course.getTeacher().equals(userId))
            throw new DataConflictException("User is a teacher in the course.");
        if (course.getStudents().contains(userId))
            throw new DataConflictException("User is already a student in the course.");

        course.getStudents().add(userId);
        student.getCourses().put(courseId, User.Role.STUDENT);
        courseRepo.save(course);
        userRepo.save(student);

        cancelEntry(userId, courseId);
    }

    /**
     * Helper method to log request/invite
     * @param userId the id of the user
     * @param courseId the id of the course
     * @param first the list to check for user-course link
     * @param second the list to add the user-course link
     */
    private void createEntry(
            String userId,
            String courseId,
            Lambda<Invitable, HashSet<String>> first,
            Lambda<Invitable, HashSet<String>> second) {
        User user = userSvc.retrieveUser(userId);
        Course course = retrieveCourse(courseId);
        assert user != null && course != null;

        userId = user.getId();
        courseId = course.getId();

        if (course.isArchived())
            throw new DataConflictException("Cannot create entry to archived course.");
        if (course.getTeacher().equals(userId))
            throw new DataConflictException("User is the teacher of the course.");
        if (course.getStudents().contains(userId))
            throw new DataConflictException("User is already a student of the course.");

        if (first.exec(user).contains(courseId) || first.exec(course).contains(userId)) {
            addStudent(userId, courseId);
            return;
        }

        second.exec(user).add(courseId);
        second.exec(course).add(userId);
        userRepo.save(user);
        courseRepo.save(course);
    }

    /**
     * Adds a user request to join the course
     * @param userId the id of the user
     * @param courseId the id of the course
     */
    public void requestEntry(String userId, String courseId) {
        createEntry(userId, courseId, obj -> obj.getInvites(), obj -> obj.getRequests());
    }

    /**
     * Invite a user to join the course
     * @param userId the id of the user
     * @param courseId the id of the course
     */
    public void offerEntry(String userId, String courseId) {
        createEntry(userId, courseId, obj -> obj.getRequests(), obj -> obj.getInvites());
    }

    /**
     * Removes a user's request/invitation to join the course
     * NOTE: you can't be requesting to join AND invited to join at the same time.
     * @param studentId the id of the user
     * @param courseId the id of the course
     */
    public void cancelEntry(String studentId, String courseId) {
        User student = userSvc.retrieveUser(studentId);
        Course course = retrieveCourse(courseId);
        assert student != null && course != null;

        studentId = student.getId();
        courseId = course.getId();

        student.getRequests().remove(courseId);
        student.getInvites().remove(courseId);
        userRepo.save(student);

        course.getRequests().remove(studentId);
        course.getInvites().remove(studentId);
        courseRepo.save(course);
    }

}
