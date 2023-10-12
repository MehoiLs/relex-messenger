package root.general.main.exceptions;

public class ProfilePictureUploadException extends Exception {
    public ProfilePictureUploadException() {
        super();
    }

    public ProfilePictureUploadException(String message) {
        super(message);
    }

    public ProfilePictureUploadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfilePictureUploadException(Throwable cause) {
        super(cause);
    }

    protected ProfilePictureUploadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
