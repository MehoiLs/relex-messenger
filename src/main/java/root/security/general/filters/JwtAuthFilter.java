package root.security.general.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import root.main.exceptions.TokenIsInvalidatedException;
import root.main.exceptions.TokenNotFoundException;
import root.main.utils.AppUtils;
import root.security.general.components.JwtAuthenticationProvider;

import java.io.IOException;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider JWTAuthenticationProvider;

    @Autowired
    public JwtAuthFilter(JwtAuthenticationProvider JWTAuthenticationProvider) {
        this.JWTAuthenticationProvider = JWTAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = AppUtils.extractTokenFromRequest(request);
        if (token != null) {
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        JWTAuthenticationProvider.validateToken(token));
            } catch (AuthenticationServiceException | TokenNotFoundException | TokenIsInvalidatedException e) {
                log.info(e.getMessage());
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        filterChain.doFilter(request, response);
    }
}
