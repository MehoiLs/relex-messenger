package root.general.security.general.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import root.general.main.data.dto.DefaultMessageDTO;
import root.general.main.data.User;
import root.general.security.general.services.AdminService;

@Hidden
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/database/cleanup")
    public ResponseEntity<DefaultMessageDTO> forceCleanupDatabase(@AuthenticationPrincipal User admin) {
        adminService.forceCleanupDatabase(admin);
        return new ResponseEntity<>(new DefaultMessageDTO("Forced cleanup database."), HttpStatus.OK);
    }

    @PostMapping("/users/logout/all")
    public ResponseEntity<DefaultMessageDTO> forceLogoutAllUsers(@AuthenticationPrincipal User admin) {
        adminService.forceLogoutAllUsers(admin);
        return new ResponseEntity<>(new DefaultMessageDTO("Forced logout all users."), HttpStatus.OK);
    }

}
