package com.mehoil.relex.general.features.messaging.controllers.chat;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.shared.utils.AppUtils;
import com.mehoil.relex.general.security.general.components.JwtAuthenticationProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Tag(
        name = "Страницы чата и логина",
        description = "Возвращает страницы для чата, логина и обрабатывает запросы для логина.")
public class ChatPageController {

    private final JwtAuthenticationProvider authenticationProvider;
    private final UserService userService;

    public ChatPageController(JwtAuthenticationProvider authenticationProvider,
                              UserService userService) {
        this.authenticationProvider = authenticationProvider;
        this.userService = userService;
    }

    @Operation(
            summary = "Перейти на страницу для логина",
            description = "Возвращает страницу для логина в чат."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Страница для логина успешно получена.",
            content = @Content(mediaType = "text/html")
    )
    @GetMapping("/login/gui")
    public String getLoginChatPage() {
        return "gui_login";
    }

    @Operation(
            summary = "Отправить данные для авторизации.",
            description = "Обрабатывает полученные данные об авторизации (JWT токен)."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно авторизировался и был перенаправлен на страницу" +
                    "чата, получив куки, содержащую информацию о его токене. Если же пользователь ввёл " +
                    "некорректные данные, вернётся страница с ошибкой \"error\".",
            content = @Content(mediaType = "text/html")
    )
    @PostMapping("/login/gui")
    public String doLoginChat(@RequestParam String token, HttpServletResponse response, Model model) {
        try {
            Authentication auth = authenticationProvider.validateToken(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
            User user = userService.getUserByAuth(auth);
            model.addAttribute("user", user);

            response.addCookie(
                    authenticationProvider.createCookieByToken(token)
            );

            return "redirect:/chat";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/error";
        }
    }

    @Operation(
            summary = "Перейти на страницу чата",
            description = "Возвращает страницу чата."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Страница для чата успешно получена. Если данные " +
                    "пользователя (куки) не были успешно обработаны сервером (неверный " +
                    "или просроченный JWT токен), вернёт страницу с ошибкой \"error\".",
            content = @Content(mediaType = "text/html")
    )
    @GetMapping("/chat")
    public String getChatPage(HttpServletRequest request, Model model) {
        String token = AppUtils.extractTokenFromCookie(request);
        User user = authenticationProvider.getUserOrNullByToken(token);
        if (user != null) {
            model.addAttribute("user", user);
            return "chat";
        } else {
            model.addAttribute("error", "Could not authorize.");
            return "error";
        }
    }

}

