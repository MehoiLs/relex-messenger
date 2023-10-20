package com.mehoil.relex.general.security.general.filters;

import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.mehoil.relex.shared.utils.AppUtils;
import com.mehoil.relex.general.security.general.components.JwtAuthenticationProvider;
import com.mehoil.relex.general.security.general.exceptions.TokenIsInvalidatedException;
import com.mehoil.relex.general.security.general.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.security.utils.WebSecurityUtils;

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
