package com.mehoil.relex.general.features.messaging.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChatServiceException extends RuntimeException {
    public ChatServiceException(String message) {
        super(message);
    }
}

