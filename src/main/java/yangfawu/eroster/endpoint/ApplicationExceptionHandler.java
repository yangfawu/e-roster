package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yangfawu.eroster.exception.ApiExecutionException;
import yangfawu.eroster.exception.ForbiddenException;
import yangfawu.eroster.exception.InputValidationException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletResponse response, Exception ex) throws IOException {
        log.error("GENERIC[{}] {}", ex.getClass(), ex.getMessage());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(ApiExecutionException.class)
    public void handleException(HttpServletResponse response, ApiExecutionException ex) throws IOException {
        log.error("[ApiExecutionException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public void handleException(HttpServletResponse response, HttpMessageNotReadableException ex) throws IOException {
        log.error("[HttpMessageNotReadableException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Payload does not conform to server requirements.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public void handleException(HttpServletResponse response, HttpMediaTypeNotSupportedException ex) throws IOException {
        log.error("[HttpMediaTypeNotSupportedException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Payload media type is not supported.");
    }

    @ExceptionHandler(InputValidationException.class)
    public void handleException(HttpServletResponse response, InputValidationException ex) throws IOException {
        log.error("[InputValidationException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public void handleException(HttpServletResponse response, ForbiddenException ex) throws IOException {
        log.error("[ForbiddenException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public void handleException(HttpServletResponse response, NoSuchElementException ex) throws IOException {
        log.error("[NoSuchElementException] {}", ex.getMessage());
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
    }

}
