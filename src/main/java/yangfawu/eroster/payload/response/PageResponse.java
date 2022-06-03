package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
public class PageResponse<T> {

    public static <T, S> PageResponse<S> from(Page<T> result, List<S> data) {
        return PageResponse.<S>builder()
                .page(result.getNumber())
                .totalPages(result.getTotalPages())
                .totalItems(result.getTotalElements())
                .result(data)
                .build();
    }

    private long page, totalPages, totalItems;
    private List<T> result;

}
