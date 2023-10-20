package com.mehoil.relex.general.security.general.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import com.mehoil.relex.shared.utils.AppUtils;
import com.mehoil.relex.general.security.general.services.InvalidatedJwtTokensService;

import java.io.IOException;
import java.util.Map;


@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final InvalidatedJwtTokensService tokensService;
    private final JwtAuthenticationProvider authenticationProvider;

    public CustomLogoutHandler(InvalidatedJwtTokensService tokensService, JwtAuthenticationProvider authenticationProvider) {
        this.tokensService = tokensService;
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = AppUtils.extractTokenFromRequest(request);
        authenticationProvider.deactivateUserSessionByToken(token);
        tokensService.invalidateToken(token);

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
    }

    public void logoutSuccessHandler(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Authentication authentication) {
        ObjectMapper objectMapper = new ObjectMapper();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        try {
            String jsonResponse;
            if(AppUtils.extractTokenFromRequest(request) != null)
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("message", "You have successfully logged out."));
            else {
                jsonResponse = objectMapper.writeValueAsString(
                        Map.of("error", "Cannot log out if the token is null."));
                response.setStatus(400);
            }
            response.getWriter().write(jsonResponse);
        } catch (IOException ignored) {}
    }
}
