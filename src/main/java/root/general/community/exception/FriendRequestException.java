package root.general.community.exception;

public class FriendRequestException extends Exception {

    public FriendRequestException() {
        super();
    }

    public FriendRequestException(String message) {
        super(message);
    }

    public FriendRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public FriendRequestException(Throwable cause) {
        super(cause);
    }

    protected FriendRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
