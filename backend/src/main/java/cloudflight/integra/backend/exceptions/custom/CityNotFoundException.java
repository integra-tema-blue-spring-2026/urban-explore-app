package cloudflight.integra.backend.exceptions.custom;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String message) {
        super(message);
    }
}
