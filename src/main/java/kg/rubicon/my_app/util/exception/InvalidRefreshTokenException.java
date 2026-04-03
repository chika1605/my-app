package kg.rubicon.my_app.util.exception;

import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends ApplicationException {
    public InvalidRefreshTokenException(String message) {
        super(message, "INVALID_REFRESH_TOKEN", HttpStatus.UNAUTHORIZED);
    }
}