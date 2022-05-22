package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.HashSet;

@Document(collection = "users")
@Data
public class User implements Invitable {

    public enum Role {
        STUDENT,
        TEACHER
    }

    @Id
    private String id;

    private String name;

    @Indexed(unique = true)
    private String email;

    private HashMap<String, Role> courses;
    private HashSet<String> invites, requests;

    public User() {
        super();
        this.courses = new HashMap<>();
        this.invites = new HashSet<>();
        this.requests = new HashSet<>();
    }

    public User(String name, String email) {
        this();
        this.name = name;
        this.email = email;
    }
}
