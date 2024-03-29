package pl.logic.site.model.exception;

public class SaveError extends RuntimeException{
    public SaveError(String message) {
        super(message);
    }
}
