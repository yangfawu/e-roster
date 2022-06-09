package yangfawu.eroster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListMeta {

    public static ListMeta defaultBuild() {
        return ListMeta.builder().count(0).build();
    }

    private int count;

}
