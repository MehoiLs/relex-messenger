package root.general.security.general.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import root.general.security.general.components.JwtAuthenticationProvider;
import root.general.security.general.data.dto.CredentialsDTO;
import root.general.security.utils.WebSecurityUtils;

import java.io.IOException;

@Slf4j
public class UsernamePasswordAuthFilter extends OncePerRequestFilter {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final JwtAuthenticationProvider JWTAuthenticationProvider;

    @Autowired
    public UsernamePasswordAuthFilter(JwtAuthenticationProvider JWTAuthenticationProvider) {
        this.JWTAuthenticationProvider = JWTAuthenticationProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (WebSecurityUtils.isIgnoreTokenRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("/sign_in".equals(request.getServletPath()) && HttpMethod.POST.matches(request.getMethod())) {
            CredentialsDTO credentialsDTO = MAPPER.readValue(
                    request.getInputStream(), CredentialsDTO.class);
            try {
                SecurityContextHolder.getContext().setAuthentication(
                        JWTAuthenticationProvider.validateCredentials(credentialsDTO));
            } catch (BadCredentialsException badCredentialsException) {
                log.info("[UsernamePasswordAuthFilter] Could not validate a user with login: " + credentialsDTO.getLogin());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                SecurityContextHolder.clearContext();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
