package yangfawu.eroster.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import yangfawu.eroster.model.Attendance;

import java.time.Instant;

@Data
@AllArgsConstructor
public class SimpleAttendanceResponse {

    private Instant created;
    private Attendance.Mark mark;

}
