package root.general.security.registration.controllers;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import root.general.main.data.User;
import root.general.security.general.exceptions.RegistrationException;
import root.general.main.services.user.UserService;
import root.general.security.registration.services.RegistrationService;

@Slf4j
@RestController
@RequestMapping("/register")
public class RegistrationController {

    private final UserService userService;
    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(UserService userService, RegistrationService registrationService) {
        this.userService = userService;
        this.registrationService = registrationService;
    }

    @PostMapping
    public ResponseEntity<?> registerUser (@RequestBody User user) {
        try {
            String resultMsg = registrationService.registerUser(user);
            return new ResponseEntity<>(resultMsg, HttpStatus.ACCEPTED);
        } catch (RegistrationException registrationException) {
            return new ResponseEntity<>("Unsuccessful registration: " +
                    registrationException.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (ConstraintViolationException constraintViolationException) {
            return new ResponseEntity<>("Unsuccessful registration: Invalid E-Mail.",
                    HttpStatus.BAD_REQUEST);
        } catch (Exception exception) {
            return new ResponseEntity<>("Unsuccessful registration. Please, try again later.",
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/confirm/{token}")
    public ResponseEntity<String> completeRegistration (@PathVariable String token) {
        return registrationService.confirmAccount(token)
            ? new ResponseEntity<>("Your account has been activated", HttpStatus.OK)
            : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
