package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.HashMap;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance extends AbstractIdEntity {

    public enum AttendanceMark {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED,
        NA
    }

    private String courseId, authorId;
    private boolean finalized;
    private Instant created, updated;
    private HashMap<String, AttendanceMark> marks;

}
