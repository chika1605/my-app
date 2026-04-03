package kg.rubicon.my_app.util.exception.handler;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        int status,
        String code,
        String message,
        String path,
        Instant timestamp,
        List<ValidationError> fieldErrors
) {
    public record ValidationError(String field, String message) {}
}
