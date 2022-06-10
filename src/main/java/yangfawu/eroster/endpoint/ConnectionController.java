package yangfawu.eroster.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/connection")
public class ConnectionController {

    public void submitInvitation(String courseId) {
        // get course by ID
        // make sure user ID == teacherId
        // make sure course is not already archived
        // check if the user is already in the course
        // check if the course already has a request from user -> if yes, add user immediately
        // create invitation reference in user
    }

    public void acceptInvitation(String courseId) {
        // find invitation reference by courseId
        // get course by ID
        // check that the course is not already archived
        // check that the user is not already in the course
        // add user into course
        // remove invitation reference
    }

    public void submitRequest(String courseId) {
        // get course by ID
        // make sure the user is a STUDENT
        // make sure the course is not already archived
        // check if the user is already in the course
        // check if the user already has an invitation -> if yes, add user immediately
        // create request reference in course
    }

    public void acceptRequest(String courseId, String userId) {
        // find request reference in course [courseId] by userId
        // get course by ID
        // check user ID == teacherId
        // check course is not already archived
        // check student is not already in course
        // add user to course
        // delete request
    }

    public Object getInvitations(int start, int size) {
        // read user's invitations to courses based on starting index and page size
        return null;
    }

    public Object getRequests(String courseId, int start, int size) {
        // get course by ID
        // make sure user is the teacher
        // fetch all requests based on starting index and page size
        return null;
    }

}
