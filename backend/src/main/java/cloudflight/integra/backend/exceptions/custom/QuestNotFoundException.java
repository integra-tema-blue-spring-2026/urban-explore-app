package cloudflight.integra.backend.exceptions.custom;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class QuestNotFoundException extends RuntimeException {
    public QuestNotFoundException(String message) {
        super(message);
    }
}
