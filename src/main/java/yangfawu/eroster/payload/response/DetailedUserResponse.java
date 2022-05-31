package yangfawu.eroster.payload.response;

import lombok.Builder;
import lombok.Data;
import yangfawu.eroster.model.PublicUser;

@Data
@Builder
public class DetailedUserResponse {

    private String id, name, school, avatarUrl, email;
    private PublicUser.Role role;

    public static DetailedUserResponse from(PublicUser user, String email) {
        return DetailedUserResponse.builder()
                .id(user.getId())
                .email(email)
                .name(user.getName())
                .school(user.getSchool())
                .avatarUrl(user.getAvatarUrl())
                .role(user.getRole())
                .build();
    }

}
