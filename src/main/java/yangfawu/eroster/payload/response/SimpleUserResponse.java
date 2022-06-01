package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;
import yangfawu.eroster.model.PublicUser;

@Data
@Builder
public class SimpleUserResponse implements IUserResponse {

    private String id, name, school, avatarUrl;

    public static SimpleUserResponse from(PublicUser user) {
        return SimpleUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .school(user.getSchool())
                .build();
    }

}
