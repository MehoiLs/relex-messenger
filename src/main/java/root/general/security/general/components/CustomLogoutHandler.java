package root.general.security.general.components;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import root.general.main.utils.AppUtils;
import root.general.security.general.services.InvalidatedJwtTokensService;


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
}
