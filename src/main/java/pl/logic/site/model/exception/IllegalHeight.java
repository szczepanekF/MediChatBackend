package pl.logic.site.model.exception;

/**
 * This class is used to represent an exception that is thrown when the height of a patient
 * is negative or too high.
 *
 * @author Kacper
 */
public class IllegalHeight extends IllegalArgumentException {
    /**
     * Constructs a new IllegalHeight exception with the specified detail message.
     *
     * @param message the detail message
     */
    public IllegalHeight(String message) {
        super(message);
    }
}
