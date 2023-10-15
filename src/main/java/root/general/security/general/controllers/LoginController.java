package root.general.security.general.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import root.general.main.data.dto.DefaultMessageDTO;
import root.general.main.exceptions.UserNotFoundException;
import root.general.security.general.data.dto.CredentialsDTO;
import root.general.security.general.exceptions.UserIsNotEnabledException;
import root.general.security.general.services.AuthenticationService;

@RestController
@Tag(
        name = "Вход в аккаунт",
        description = "Производит валидацию данных пользователей (логин и пароль), создаёт JWT токен.")
public class LoginController {

    private final AuthenticationService authenticationService;

    @Autowired
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
    public ResponseEntity<DefaultMessageDTO> login(@RequestBody CredentialsDTO credentials) {
        try {
            String authResult = authenticationService.authenticateUserByCredentials(credentials);
            return new ResponseEntity<>(new DefaultMessageDTO(authResult), HttpStatus.OK);
        } catch (BadCredentialsException | UserNotFoundException e) {
            return new ResponseEntity<>(new DefaultMessageDTO("Incorrect login or password."), HttpStatus.BAD_REQUEST);
        } catch (UserIsNotEnabledException userIsNotEnabledException) {
            return new ResponseEntity<>(new DefaultMessageDTO("Your account is not enabled."), HttpStatus.UNAUTHORIZED);
        }
    }
}
