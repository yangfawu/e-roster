package yangfawu.eroster.util;

import org.springframework.web.util.UriComponentsBuilder;

public class UiAvatar {

    public static final String create(String name) {
        return UriComponentsBuilder.fromUriString("https://ui-avatars.com/api/")
                                    .queryParam("name", name)
                                    .queryParam("size", 128)
                                    .build()
                                    .toUriString();
    }

}
