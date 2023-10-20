package com.mehoil.relex.general.security.general.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.mehoil.relex.database.exceptions.UserNotFoundException;
import com.mehoil.relex.general.security.general.data.dto.CredentialsDTO;
import com.mehoil.relex.general.security.general.data.dto.LoginDTO;
import com.mehoil.relex.general.security.general.exceptions.UserIsNotEnabledException;
import com.mehoil.relex.general.security.general.services.AuthenticationService;

@RestController
@Tag(
        name = "Вход в аккаунт",
        description = "Производит валидацию данных пользователей (логин и пароль), создаёт JWT токен.")
public class LoginController {

    private final AuthenticationService authenticationService;

    public LoginController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(
            summary = "Зарегистрировать пользователя",
            description = "Производит регистрацию пользователя и отправляет ему письмо о подтверждении."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Предоставленные данные были успешно валидированы. Пользователю возвращён JWT токен, " +
                    "который будет действителен в течении следующих 24 часов." +
                    "Если же предоставленные данные были некорректными, будет возвращен код состояния `BAD_REQUEST`. " +
                    "Если же пользователь, попытавшийся войти, является не подтверждённым, " +
                    "будет возвращен код состояния `UNAUTHORIZED`.",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping("/login")
    public ResponseEntity<LoginDTO> login(@RequestBody CredentialsDTO credentials) throws UserNotFoundException, UserIsNotEnabledException {
        LoginDTO authResult = authenticationService.authenticateUserByCredentials(credentials);
        return new ResponseEntity<>(authResult, HttpStatus.OK);
    }
}
