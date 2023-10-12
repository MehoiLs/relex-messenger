package root.general.main.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/home")
    ResponseEntity<String> getHello() {
        return new ResponseEntity<>("Hello! This is a home page.", HttpStatus.OK);
    }

}
