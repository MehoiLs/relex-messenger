package com.mehoil.relex.general.security.general.exceptions.handler;

import com.mehoil.relex.shared.dto.DefaultErrorMessageDTO;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.security.general.exceptions.UserIsNotEnabledException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(basePackages = "com.mehoil.relex.general.security")
public class SecurityExceptionHandler {

    private final MessageSource messageSource;

    public SecurityExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleBadCredentialsException(DatabaseRecordNotFoundException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(
                        messageSource.getMessage("credentials-incorrect-login-or-password", null, Locale.getDefault())
                ),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserIsNotEnabledException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleUserIsNotEnabledException(DatabaseRecordNotFoundException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(
                        messageSource.getMessage("user-account-not-enabled", null, Locale.getDefault())
                ),
                HttpStatus.UNAUTHORIZED);
    }

}
