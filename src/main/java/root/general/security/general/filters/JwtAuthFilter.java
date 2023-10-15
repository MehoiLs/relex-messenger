package root.general.security.general.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import root.general.main.utils.AppUtils;
import root.general.security.general.components.JwtAuthenticationProvider;
import root.general.security.general.exceptions.TokenIsInvalidatedException;
import root.general.security.general.exceptions.TokenNotFoundException;
import root.general.security.utils.WebSecurityUtils;

import java.io.IOException;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider authenticationProvider;

    public JwtAuthFilter(JwtAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (WebSecurityUtils.isIgnoreTokenRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = AppUtils.extractTokenFromRequest(request);
        if (token != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        authenticationProvider.validateToken(token));
            } catch (AuthenticationServiceException | TokenNotFoundException
                     | TokenIsInvalidatedException | TokenExpiredException e) {
                log.info("[JWT FILTER] Token validation failed: \"" + token + "\". " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth failed:\n" + e.getMessage());
                SecurityContextHolder.clearContext();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
