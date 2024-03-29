package pl.logic.site.model.exception;

public class DeleteError extends RuntimeException{
    public DeleteError(String message) {
        super(message);
    }
}
