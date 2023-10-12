package root.general.security.general.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import root.general.security.general.data.dto.CredentialsDTO;
import root.general.security.general.exceptions.UserIsNotEnabledException;
import root.general.security.general.components.CustomLogoutHandler;
import root.general.security.general.services.AuthenticationService;

@Slf4j
@RestController
public class LoginController {

    private final AuthenticationService authenticationService;
    private final CustomLogoutHandler logoutHandler;

    @Autowired
    public LoginController(AuthenticationService authenticationService, CustomLogoutHandler logoutHandler) {
        this.authenticationService = authenticationService;
        this.logoutHandler = logoutHandler;
    }

    @PostMapping("/login")
    public ResponseEntity<String> logIn(@RequestBody CredentialsDTO credentials) {
        try {
            String authResult = authenticationService.authenticateUserByCredentials(credentials);
            return new ResponseEntity<>(authResult, HttpStatus.OK);
        } catch (BadCredentialsException badCredentialsException) {
            log.info("There was a login attempt by user: " + credentials.getLogin());
            return new ResponseEntity<>("Incorrect password.", HttpStatus.BAD_REQUEST);
        } catch (UserIsNotEnabledException userIsNotEnabledException) {
            log.info("There was a login attempt by a non-enabled user: " + credentials.getLogin());
            return new ResponseEntity<>("Your account is not enabled.", HttpStatus.UNAUTHORIZED);
        }
    }
}
