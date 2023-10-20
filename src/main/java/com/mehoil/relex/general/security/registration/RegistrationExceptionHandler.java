package com.mehoil.relex.general.security.registration;

import com.mehoil.relex.general.security.general.exceptions.RegistrationException;
import com.mehoil.relex.general.security.general.exceptions.TokenNotFoundException;
import com.mehoil.relex.shared.dto.DefaultErrorMessageDTO;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice(basePackages = "com.mehoil.relex.general.security.registration")
public class RegistrationExceptionHandler {

    private final MessageSource messageSource;

    public RegistrationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleRegistrationException(RegistrationException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(
                        messageSource.getMessage("registration-unsuccessful", null, Locale.getDefault()) + " " +
                                e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleTokenNotFoundException(TokenNotFoundException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

}