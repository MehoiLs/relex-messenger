package com.mehoil.relex.general.security.general.controllers;

import com.mehoil.relex.general.security.general.services.AdminService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mehoil.relex.shared.dto.DefaultMessageDTO;
import com.mehoil.relex.general.user.data.User;

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
        String msg = adminService.forceCleanupDatabase(admin);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

    @PostMapping("/users/logout/all")
    public ResponseEntity<DefaultMessageDTO> forceLogoutAllUsers(@AuthenticationPrincipal User admin) {
        String msg = adminService.forceLogoutAllUsers(admin);
        return new ResponseEntity<>(new DefaultMessageDTO(msg), HttpStatus.OK);
    }

}
