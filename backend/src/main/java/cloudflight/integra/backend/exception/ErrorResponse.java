package cloudflight.integra.backend.exception;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private Map<String, String> fieldErrors;
}
