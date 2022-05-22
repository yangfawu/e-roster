package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;

@Document(collection = "courses")
@Data
public class Course implements Invitable {

    @Id
    private String id;

    private String teacher, name, description;
    private HashSet<String> students, invites, requests, attendances;
    private boolean archived;

    public Course() {
        super();
        this.students = new HashSet<>();
        this.invites = new HashSet<>();
        this.requests = new HashSet<>();
        this.attendances = new HashSet<>();
        this.archived = false;
    }

    public Course(String teacher, String name, String description) {
        this();
        this.teacher = teacher;
        this.name = name;
        this.description = description;
    }

    public Course(String name) {
        this("NO_TEACHER_ID", name, "NO_DESCRIPTION");
    }

}
