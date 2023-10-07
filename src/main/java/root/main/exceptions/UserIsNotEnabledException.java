package root.main.exceptions;

public class UserIsNotEnabledException extends Exception {
    public UserIsNotEnabledException() {
        super();
    }

    public UserIsNotEnabledException(String message) {
        super(message);
    }

    public UserIsNotEnabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserIsNotEnabledException(Throwable cause) {
        super(cause);
    }

    protected UserIsNotEnabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
