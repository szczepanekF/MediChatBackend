package pl.logic.site.model.exception;

public class EmailOrUsernameJustExist extends RuntimeException{
    public EmailOrUsernameJustExist(String message) {
        super(message);
    }
}
