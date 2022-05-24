package yangfawu.eroster.endpoint;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestControllerAdvice
@Log4j2
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            Exception exception) throws IOException {
        log.error("{} :: {}", request.getRequestURL(), exception.getMessage());
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }

}
