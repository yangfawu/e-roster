package yangfawu.eroster.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;

@Document(collection = "attendances")
@Data
public class Attendance {

    public enum Status {
        PRESENT,
        LATE,
        EXCUSED,
        ABSENT,
        N_A
    }

    @Id
    private String id;

    private String courseId;
    private LocalDateTime timeCreated, timeCreatedFor, lastUpdated;
    private HashMap<String, Status> marks;
    private boolean archived;

    public Attendance() {
        this.timeCreated = LocalDateTime.now();
        this.timeCreatedFor = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
        this.marks = new HashMap<>();
        this.archived = false;
    }

    public Attendance(String courseId) {
        this();
        this.courseId = courseId;
    }

    public void setMsTimeCreatedFor(long msTimeCreatedFor) {
        this.timeCreatedFor = Instant.ofEpochMilli(msTimeCreatedFor)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

}
