package yangfawu.eroster.endpoint;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/private/course")
public class CourseController {

    public Object getCourseIds(int start, int size) {
        // get the course IDs affiliated with the user based on starting index and page size
        return null;
    }

    public Object getStudentIds(int courseId, int start, int size) {
        // get course by ID
        // check that the user is the teacher or in the class
        // get student Ids ased on starting index and page size
        return null;
    }

    public Object getReducedCourse(String id) {
        // get basic course info [id, name, teacherId] by ID
        return null;
    }

    public Object getCourse(String id) {
        // get course by ID
        // check the user is the teacher or in the class
        // get full information about a course
        return null;
    }

    public Object createCourse(Object data) {
        // check that the maker is a teacher
        // validate the data
        // create the course
        // add course reference to user
        // get the newly created course
        return null;
    }

    public void archiveCourse(String id) {
        // get course by ID
        // make sure user's ID is the teacherId
        // archive course
    }

    public void updateCourse(Object newCourseInfo) {
        // get course by ID
        // make sure user's ID is the teacherId
        // make sure course is not archived
        // validate data
        // update the course
    }

}
