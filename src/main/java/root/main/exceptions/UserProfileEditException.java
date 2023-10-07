package root.main.exceptions;

public class UserProfileEditException extends Exception {

    public UserProfileEditException() {
        super();
    }

    public UserProfileEditException(String message) {
        super(message);
    }

    public UserProfileEditException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserProfileEditException(Throwable cause) {
        super(cause);
    }

    protected UserProfileEditException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
