package cloudflight.integra.backend.domain.dtos;


import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class ApiErrorResponse {
    private LocalDateTime timestamp;
    private HttpStatus status;
    private Map<String,String> errors;
    private String message;
    private String path;
}
