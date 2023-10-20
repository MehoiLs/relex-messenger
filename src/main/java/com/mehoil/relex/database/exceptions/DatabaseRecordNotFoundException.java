package com.mehoil.relex.database.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DatabaseRecordNotFoundException extends Exception {

    public DatabaseRecordNotFoundException() {
        super();
    }

    public DatabaseRecordNotFoundException(String message) {
        super(message);
    }

    public DatabaseRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatabaseRecordNotFoundException(Throwable cause) {
        super(cause);
    }

    protected DatabaseRecordNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
