package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "requests")
@Data
public class StudentRequest {

    private static final String TICKET_FORMAT = "%s_enter_%s";

    @Id
    private String id;

    private String studentId, courseId, ticket;

    private Instant created;

    public StudentRequest(String studentId, String courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.ticket = String.format(TICKET_FORMAT, studentId, courseId);
        this.created = Instant.now();
    }
}
