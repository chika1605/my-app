package kg.rubicon.my_app.util.exception;

import org.springframework.http.HttpStatus;

public class MLIntegrationException extends ApplicationException {

    public MLIntegrationException(String message, String errorCode, HttpStatus httpStatus) {
        super(message, errorCode, httpStatus);
    }

    public MLIntegrationException(String message, String errorCode, HttpStatus httpStatus, Throwable cause) {
        super(message, errorCode, httpStatus, cause);
    }

}
