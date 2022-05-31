package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "invitations")
@Data
public class CourseInvitation {

    private static final String TICKET_FORMAT = "%s_invites_%s";

    @Id
    private String id;

    private String courseId, inviteeId, ticket;

    private Instant created;

    public CourseInvitation(String courseId, String inviteeId) {
        this.courseId = courseId;
        this.inviteeId = inviteeId;
        this.ticket = String.format(TICKET_FORMAT, courseId, inviteeId);
        this.created = Instant.now();
    }

}
