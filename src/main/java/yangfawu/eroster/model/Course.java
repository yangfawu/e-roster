package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Course {

    private String id, name, description, teacherId;
    private boolean archived;
    private Instant created;

}
