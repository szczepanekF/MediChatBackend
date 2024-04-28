package pl.logic.site.model.exception;

/**
 * This class is used to represent an exception that is thrown when the weight of a patient is negative or to high.
 *
 * @author Kacper
 */
public class IllegalWeight  extends IllegalArgumentException {
    /**
     * Constructs a new IllegalWeight exception with the specified detail message.
     *
     * @param message the detail message
     */
    public IllegalWeight(String message) {
        super(message);
    }
}
