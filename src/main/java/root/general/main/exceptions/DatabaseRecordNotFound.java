package root.general.main.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DatabaseRecordNotFound extends Exception {

    public DatabaseRecordNotFound() {
        super();
    }

    public DatabaseRecordNotFound(String message) {
        super(message);
    }

    public DatabaseRecordNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseRecordNotFound(Throwable cause) {
        super(cause);
    }

    protected DatabaseRecordNotFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
