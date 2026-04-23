package cloudflight.integra.backend.exceptions;

import cloudflight.integra.backend.exceptions.custom.*;
import cloudflight.integra.backend.exceptions.custom.user.UserFollowException;
import cloudflight.integra.backend.exceptions.custom.user.UserUnfollowException;
import cloudflight.integra.backend.model.dtos.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex,
                                                                        HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach((fieldError) -> {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        });

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST)
            .errors(errors)
            .message("Validation Error")
            .path(request.getRequestURI()).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
        DataIntegrityViolationException ex,
        HttpServletRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT)
            .errors(Map.of("database", "Data integrity violation"))
            .message("Database Error")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .errors(Map.of(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()) ,"Internal server error"))
            .message("An unexpected internal server error occurred.")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(CityNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCityNotFound(CityNotFoundException ex,HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND)
            .errors(Map.of("404", "Resource not found"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);

    }

    @ExceptionHandler(ReviewException.class)
    public ResponseEntity<ApiErrorResponse> handleReviewException(
        ReviewException ex, HttpServletRequest request) {
        HttpStatus status = resolveReviewExceptionStatus(ex);
        String errorMessage = status == HttpStatus.BAD_REQUEST ?
            "Invalid review request" : "Review not found";
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status)
            .errors(Map.of(String.valueOf(status.value()), errorMessage))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    private HttpStatus resolveReviewExceptionStatus(ReviewException ex) {
        String message = ex.getMessage();
        if (message == null) {
            return HttpStatus.NOT_FOUND;
        }
        String normalizedMessage = message.toLowerCase();
        if (normalizedMessage.contains("invalid")
            || normalizedMessage.contains("missing")
            || normalizedMessage.contains("required")
            || normalizedMessage.contains("must not be null")) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.NOT_FOUND;
    }

    @ExceptionHandler(QuestNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleQuestNotFound(
        QuestNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND)
            .errors(Map.of("404", "Quest not found"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PointOfInterestNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePoiNotFound(
        PointOfInterestNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.NOT_FOUND)
            .errors(Map.of("404", "Point of Interest not found"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserFollowException.class)
    public ResponseEntity<ApiErrorResponse> handleUserFollowException(
        UserFollowException ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST)
            .errors(Map.of("400", "User follow error"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UserUnfollowException.class)
    public ResponseEntity<ApiErrorResponse> handleUserUnfollowException(
        UserUnfollowException ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST)
            .errors(Map.of("400", "User unfollow error"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(
        UsernameNotFoundException ex, HttpServletRequest request) {
        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED)
            .errors(Map.of("401", "Bad credentials"))
            .message("Invalid username or password")
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleBadCredentialsException(
        BadCredentialsException ex, HttpServletRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.UNAUTHORIZED)
            .errors(Map.of("401", "Authentication failed"))
            .message("Invalid username or password")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UpdateCityException.class)
    public ResponseEntity<ApiErrorResponse> handleUpdateCityBadRequest(
        UpdateCityException ex,
        HttpServletRequest request) {

        ApiErrorResponse errorResponse = ApiErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST)
            .errors(Map.of("400", "Invalid input provided"))
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
