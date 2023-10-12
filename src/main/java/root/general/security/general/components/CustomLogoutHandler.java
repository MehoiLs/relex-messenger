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

    public CustomLogoutHandler(InvalidatedJwtTokensService tokensService) {
        this.tokensService = tokensService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = AppUtils.extractTokenFromRequest(request);
        tokensService.invalidateToken(token);

        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
    }
}
