package root.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.security.general.services.InvalidatedJwtTokenCleanupService;
import root.security.registration.services.ExpiredRegistrationTokenCleanupService;

@RestController
@RequestMapping("/home")
public class MainController {

    @GetMapping
    ResponseEntity<String> getHello() {
        return new ResponseEntity<>("Hello!", HttpStatus.OK);
    }

}
