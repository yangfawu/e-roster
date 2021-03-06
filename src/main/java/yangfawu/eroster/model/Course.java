package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Course extends AbstractIdEntity {

    private String name, description, teacherId;
    private boolean archived;
    private Instant created;

}
