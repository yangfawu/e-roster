package yangfawu.eroster.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashMap;

@Document(collection = "attendances")
public class Attendance {

    public enum Mark {
        PRESENT,
        LATE,
        ABSENT,
        EXCUSED,
        UNMARKED
    }

    @Id
    private String id;

    private String courseId;

    private Instant created, updated;

    private boolean archived;

    private HashMap<String, Mark> marks;

    public Attendance(String courseId) {
        this.courseId = courseId;
        this.created = Instant.now();
        this.updated = Instant.now();
        this.archived = false;
        this.marks = new HashMap<>();
    }
}
