package pl.logic.site.model.exception;

/**
 * This exception is thrown when the given proportion is not a positive number.
 *
 * @author Kacper
 */
public class InvalidProportion extends IllegalArgumentException {
    /**
     * Constructs a new instance of this exception with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidProportion(String message) {
        super(message);
    }
}
