package cloudflight.integra.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PointOfInterestNotFoundException extends RuntimeException{
    public PointOfInterestNotFoundException(String message) {
        super(message);
    }
}
