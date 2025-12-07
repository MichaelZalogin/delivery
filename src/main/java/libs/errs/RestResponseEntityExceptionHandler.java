package libs.errs;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.slf4j.LoggerFactory.getLogger;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
    private static final Logger log = getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {RuntimeException.class})
    protected ResponseEntity<ErrorInfoDto> handleBasicExceptions(RuntimeException e, HttpServletRequest request) {
        log.warn("Error executing REST request", e);
        return error(request.getRequestURI(), e);

    }

    private ResponseEntity<ErrorInfoDto> error(String uri, RuntimeException e) {
        ErrorInfoDto errorInfoDto = ErrorInfoDtoBuilder.errorInfoDto()
                .uri(uri)
                .exception(e.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorInfoDto);
    }
}