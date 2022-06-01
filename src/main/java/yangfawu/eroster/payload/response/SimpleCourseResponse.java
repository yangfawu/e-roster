package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;
import yangfawu.eroster.model.Course;
import yangfawu.eroster.model.PublicUser;

@Data
@Builder
public class SimpleCourseResponse implements ICourseResponse {

    private String id, name;
    private boolean archived;
    private SimpleUserResponse teacher;

    public static SimpleCourseResponse from(Course course, PublicUser teacher) {
        return SimpleCourseResponse.builder()
                .id(course.getId())
                .name(course.getName())
                .archived(course.isArchived())
                .teacher(SimpleUserResponse.from(teacher))
                .build();
    }

}
