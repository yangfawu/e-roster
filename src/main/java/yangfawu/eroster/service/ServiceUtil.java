package yangfawu.eroster.service;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import org.springframework.web.util.UriComponentsBuilder;
import yangfawu.eroster.exception.ApiExecutionException;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ServiceUtil {

    public static final String create(String name) {
        return UriComponentsBuilder.fromUriString("https://ui-avatars.com/api/")
                .queryParam("name", name)
                .queryParam("size", 128)
                .encode()
                .build()
                .toUriString();
    }

    public static <T> T handleFuture(ApiFuture<T> future) {
        T data;
        try {
            data = future.get(30, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new ApiExecutionException(e.getCause().getMessage());
        }
        return data;
    }

    public static void handleFutures(ApiFuture<?>... futures) {
        handleFuture(ApiFutures.allAsList(Arrays.asList(futures)));
    }

}
