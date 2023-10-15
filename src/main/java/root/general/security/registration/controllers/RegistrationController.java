package root.general.security.registration.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import root.general.main.data.dto.DefaultMessageDTO;
import root.general.main.data.User;
import root.general.security.general.exceptions.RegistrationException;
import root.general.security.registration.services.RegistrationService;

@Slf4j
@RestController
@RequestMapping("/register")
@Tag(
        name = "Регистрация пользователей",
        description = "Производит регистрацию пользователей на основе предоставленных данных.")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Operation(
            summary = "Зарегистрировать пользователя",
            description = "Производит регистрацию пользователя и отправляет ему письмо о подтверждении."
    )
    @ApiResponse(
            responseCode = "202",
            description = "Предоставленные данные были успешно обработаны, запись о пользователе была " +
                    "добавлена в базу данных, будет произведено 3 попытки отправки письма о подтверждении аккаунта " +
                    "на указанную пользователем почту. " +
                    "Если же запись о пользователе уже существует в базе данных, но " +
                    "пользователь не является подтверждённым, будет произведено 3 попытки повторной отправки письма о " +
                    "подтверждении аккаунта. " +
                    "Если же предоставленные данные были некорректными, будет возвращен код состояния `BAD_REQUEST`",
            content = @Content(mediaType = "application/json")
    )
    @PostMapping
    public ResponseEntity<DefaultMessageDTO> registerUser (@RequestBody User user) {
        try {
            String resultMsg = registrationService.registerUser(user);
            return new ResponseEntity<>(
                    new DefaultMessageDTO(resultMsg),
                    HttpStatus.ACCEPTED);
        } catch (RegistrationException registrationException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Unsuccessful registration: " + registrationException.getMessage()),
                    HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException constraintViolationException) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Unsuccessful registration: Invalid E-Mail."),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>(
                    new DefaultMessageDTO("Unsuccessful registration. Please, try again later."),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(
            summary = "Подтвердить аккаунт пользователя",
            description = "Производит подтверждение аккаунта пользователя."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь был успешно подтверждён.",
            content = @Content(mediaType = "application/json")
    )
    @GetMapping("/confirm/{token}")
    public ResponseEntity<DefaultMessageDTO> completeRegistration (@PathVariable String token) {
        return registrationService.confirmAccount(token)
            ? new ResponseEntity<>(new DefaultMessageDTO("Your account has been activated."), HttpStatus.OK)
            : new ResponseEntity<>( new DefaultMessageDTO("Token not found."), HttpStatus.NOT_FOUND);
    }
}
