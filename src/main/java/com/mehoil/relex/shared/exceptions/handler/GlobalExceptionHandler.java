package com.mehoil.relex.shared.exceptions.handler;

import com.mehoil.relex.shared.dto.DefaultErrorMessageDTO;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Locale;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler({
        IOException.class
    })
    public ResponseEntity<DefaultErrorMessageDTO> handleCommonExceptions (Exception e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(e.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            DatabaseRecordNotFoundException.class,
            UserNotFoundException.class
    })
    public ResponseEntity<DefaultErrorMessageDTO> handleDatabaseRecordsExceptions(DatabaseRecordNotFoundException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleTokenNotFoundException (TokenNotFoundException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<DefaultErrorMessageDTO> handleNullPointerException(NullPointerException e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(
                        messageSource.getMessage("error-null-pointer", null, Locale.getDefault())
                ),
                HttpStatus.BAD_REQUEST);
    }

}
