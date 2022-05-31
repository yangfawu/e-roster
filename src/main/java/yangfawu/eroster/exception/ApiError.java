package yangfawu.eroster.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
public class ApiError {

    private final HttpStatus status;
    private final String message;

    private final long timestamp;
    private final ResponseEntity<ApiError> entity;

    public ApiError(@NonNull HttpStatus status, @NonNull String message) {
        this.status = status;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
        this.entity = new ResponseEntity<>(this, status);
    }

}
