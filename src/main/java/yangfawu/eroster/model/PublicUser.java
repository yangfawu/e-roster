package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import yangfawu.eroster.util.UiAvatar;

@Document(collection = "users")
@Data
public class PublicUser {

    @Id
    private String id;

    private String name, school, avatarUrl;
    private Role role;
    @Indexed(unique = true)
    private String privateUserId;

    public PublicUser(String name, String school, Role role) {
        this.name = name;
        this.school = school;
        this.role = role;
        this.avatarUrl = UiAvatar.create("");
    }

    public enum Role {STUDENT, TEACHER;}

}
