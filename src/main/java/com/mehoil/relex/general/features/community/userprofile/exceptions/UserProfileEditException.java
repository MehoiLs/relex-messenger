package com.mehoil.relex.general.features.community.userprofile.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserProfileEditException extends Exception {

    public UserProfileEditException(String message) {
        super(message);
    }

}
