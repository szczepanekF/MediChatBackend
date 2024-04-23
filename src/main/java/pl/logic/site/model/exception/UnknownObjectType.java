package pl.logic.site.model.exception;

public class UnknownObjectType extends RuntimeException{
    public UnknownObjectType(String message) {
        super(message);
    }
}
