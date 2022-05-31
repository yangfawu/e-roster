package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class DetailedCourseResponse {

    private String id, name, description;

    private boolean archived;

    private List<SimpleUserResponse> students;

    private SimpleUserResponse teacher;

    private Instant created;

}
