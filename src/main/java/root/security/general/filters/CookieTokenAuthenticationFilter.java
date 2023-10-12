package root.security.general.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import root.main.exceptions.TokenIsInvalidatedException;
import root.main.exceptions.TokenNotFoundException;
import root.main.utils.AppUtils;
import root.security.general.components.JwtAuthenticationProvider;

import java.io.IOException;

@Slf4j
public class CookieTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider authenticationProvider;

    @Autowired
    public CookieTokenAuthenticationFilter(JwtAuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = AppUtils.extractTokenFromCookie(request);
        if(token != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        authenticationProvider.validateToken(token));
            } catch (AuthenticationServiceException | TokenNotFoundException
                     | TokenIsInvalidatedException | TokenExpiredException e) {
                log.info("[COOKIE-JWT FILTER] Token validation failed: \"" + token + "\". " + e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Auth failed:\n" + e.getMessage());
                SecurityContextHolder.clearContext();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

}
