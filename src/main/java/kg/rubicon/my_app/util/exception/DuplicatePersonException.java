package kg.rubicon.my_app.util.exception;

import kg.rubicon.my_app.ml.dto.MlErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicatePersonException extends ApplicationException {

    private final MlErrorResponse.Details details;

    public DuplicatePersonException(MlErrorResponse.Details details) {
        super(
                "A likely duplicate person already exists; do not create a new record",
                "DUPLICATE_PERSON_DETECTED",
                HttpStatus.CONFLICT
        );
        this.details = details;
    }
}
