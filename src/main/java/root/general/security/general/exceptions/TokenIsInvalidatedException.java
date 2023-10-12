package root.general.security.general.exceptions;

public class TokenIsInvalidatedException extends Exception {
    public TokenIsInvalidatedException() {
        super();
    }

    public TokenIsInvalidatedException(String message) {
        super(message);
    }

    public TokenIsInvalidatedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenIsInvalidatedException(Throwable cause) {
        super(cause);
    }

    protected TokenIsInvalidatedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
