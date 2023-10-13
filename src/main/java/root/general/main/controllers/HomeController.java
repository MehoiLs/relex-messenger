package root.general.main.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(
        name = "Домашняя страница",
        description = "Предоставляет API для доступа к домашней странице (публичной) ")
public class HomeController {

    @Operation(
            summary = "Просмотреть домашнюю страницу",
            description = "Просмотреть домашнюю страницу (публичная).")
    @ApiResponse(
            responseCode = "200",
            description = "Информация о домашней странице успешно получена.",
            content = @Content(mediaType = "text/plain"))
    @GetMapping("/home")
    ResponseEntity<String> getHello() {
        return new ResponseEntity<>("Hello! This is a home page.", HttpStatus.OK);
    }

}
