package com.mehoil.relex.general.features.community.common.exceptions.handler;

import com.mehoil.relex.general.features.community.common.exceptions.CommunityException;
import com.mehoil.relex.shared.dto.DefaultErrorMessageDTO;
import com.mehoil.relex.general.features.community.userprofile.exceptions.ProfilePictureUploadException;
import com.mehoil.relex.general.features.community.userprofile.exceptions.UserProfileEditException;
import com.mehoil.relex.general.features.messaging.exceptions.ChatServiceException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.mehoil.relex.general.features.community")
public class CommunityControllerExceptionHandler {

    @ExceptionHandler({
            CommunityException.class,
            UserProfileEditException.class,
            ProfilePictureUploadException.class,
            ChatServiceException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<DefaultErrorMessageDTO> handleCommunityExceptions (Exception e) {
        return new ResponseEntity<>(
                new DefaultErrorMessageDTO(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

}
