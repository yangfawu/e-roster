package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashMap;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {

    public enum AttendanceMark {
        PRESENT,
        ABSENT,
        LATE,
        EXCUSED,
        NA
    }

    private int index;
    private boolean finalized;
    private Instant created, updated;
    private HashMap<String, AttendanceMark> marks;

}
