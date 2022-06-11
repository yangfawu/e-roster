package yangfawu.eroster.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import yangfawu.eroster.exception.ApiExecutionException;
import yangfawu.eroster.exception.InputValidationException;
import yangfawu.eroster.model.Connection;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final UserService userSvc;
    private final CourseService courseSvc;
    private final StudentsCollectionService studColSvc;
    private final InvitationsCollectionService invColSvc;
    private final RequestsCollectionService reqColSvc;
    private final CoursesCollectionService coursesColSvc;

    public void submitInvitation(Jwt token, String userId, String courseId) {
        Course course = courseSvc.getCourseIfTokenIsTeacher(token, courseId);
        if (course.isArchived())
            throw new InputValidationException("Cannot invite to an archived course.");

        User invitee = userSvc.getUserById(userId);
        if (invitee.getAccountType() != User.UserType.STUDENT)
            throw new InputValidationException("Invitee must be a student.");

        final String UID = invitee.getId();
        if (studColSvc.hasItem(courseId, UID))
            throw new InputValidationException("User is already a student in the course.");

        if (invColSvc.hasItem(UID, courseId))
            throw new InputValidationException("An invitation to the user has already been sent.");

        if (reqColSvc.hasItem(courseId, UID)) {
            reqColSvc.deleteItem(courseId, UID);
            studColSvc.addReference(courseId, UID);
            coursesColSvc.addReference(UID, courseId);
            return;
        }

        invColSvc.addReference(UID, courseId);
    }

    public void acceptInvite(Jwt token, String courseId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        Course course = courseSvc.getCourseById(courseId);

        if (!invColSvc.hasItem(UID, courseId))
            throw new InputValidationException("User has not been invited to the course.");
        invColSvc.deleteItem(UID, courseId);

        if (course.isArchived())
            throw new ApiExecutionException("Cannot join archived course. Removed invitation.");

        if (studColSvc.hasItem(courseId, UID))
            throw new InputValidationException("User is already a student in the course. Removed invitation.");

        studColSvc.addReference(courseId, UID);
        coursesColSvc.addReference(UID, courseId);
    }

    public void submitRequest(Jwt token, String courseId) {
        final String UID = userSvc.getUserIdFromFirebaseJwt(token);
        User requester = userSvc.getUserById(UID);
        if (requester.getAccountType() != User.UserType.STUDENT)
            throw new InputValidationException("User must be a student.");

        Course course = courseSvc.getCourseById(courseId);
        if (course.isArchived())
            throw new InputValidationException("Cannot request entry into an archived course.");

        if (studColSvc.hasItem(courseId, UID))
            throw new InputValidationException("User is already a student in the course.");

        if (reqColSvc.hasItem(courseId, UID))
            throw new InputValidationException("A request from the user has already been sent to the course.");

        if (invColSvc.hasItem(UID, courseId)) {
            invColSvc.deleteItem(UID, courseId);
            studColSvc.addReference(courseId, UID);
            coursesColSvc.addReference(UID, courseId);
            return;
        }

        reqColSvc.addReference(courseId, UID);
    }

    public void acceptRequest(Jwt token, String userId, String courseId) {
        Course course = courseSvc.getCourseIfTokenIsTeacher(token, courseId);

        if (!reqColSvc.hasItem(courseId, userId))
            throw new InputValidationException("User did request entry into the course.");
        reqColSvc.deleteItem(courseId, userId);

        if (course.isArchived())
            throw new ApiExecutionException("Cannot let users into archived course. Removed request.");

        if (studColSvc.hasItem(courseId, userId))
            throw new InputValidationException("User is already a student in the course. Removed request.");

        studColSvc.addReference(courseId, userId);
        coursesColSvc.addReference(userId, courseId);
    }


    public List<Connection> getInvitations(Jwt token, int start, int size) {
        return invColSvc.getItems(
                userSvc.getUserIdFromFirebaseJwt(token),
                start,
                size
        );
    }

    public List<Connection> getRequests(Jwt token, String courseId, int start, int size) {
        courseSvc.getCourseIfTokenIsTeacher(token, courseId);
        return reqColSvc.getItems(courseId, start, size);
    }
}
