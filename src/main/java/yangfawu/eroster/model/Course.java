package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.LinkedHashSet;

@Document(collection = "courses")
@Data
public class Course {

    @Id
    private String id;

    private String teacherId, name, description;

    private Instant created;
    private boolean archived;
    private LinkedHashSet<String> studentIds;

    public Course(String teacherId, String name, String description) {
        this.teacherId = teacherId;
        this.name = name;
        this.description = description;
        this.archived = false;
        this.created = Instant.now();
        this.studentIds = new LinkedHashSet<>();
    }
}
