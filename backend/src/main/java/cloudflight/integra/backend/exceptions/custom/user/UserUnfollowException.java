package cloudflight.integra.backend.exceptions.custom.user;

public class UserUnfollowException extends RuntimeException {
    public UserUnfollowException(String message) {
        super(message);
    }
}
