package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseListResponse {

    private long page, totalPages, totalItems;
    private List<SimpleCourseResponse> result;

}
