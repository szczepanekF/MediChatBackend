package pl.logic.site.model.exception;

/**
 * This class is used to represent an exception that is thrown when an illegal gender is provided,
 * that is, one that is not in the dictionary.
 *
 * @author Kacper
 */
public class IllegalGender extends IllegalArgumentException{
    /**
     * Constructs a new IllegalGender exception with the specified detail message.
     *
     * @param message the detail message
     */
    public IllegalGender(String message) {
        super(message);
    }
}
