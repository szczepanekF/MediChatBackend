package pl.logic.site.model.exception;

public class UnknownUserType extends RuntimeException{
    public UnknownUserType(String message) {
        super(message);
    }
}
