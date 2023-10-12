package root.general.security.general.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import root.general.security.general.components.CustomLogoutHandler;
import root.general.security.general.components.JwtAuthenticationProvider;
import root.general.security.general.filters.CookieTokenAuthenticationFilter;
import root.general.security.general.filters.JwtAuthFilter;
import root.general.security.general.filters.UsernamePasswordAuthFilter;
import root.general.security.utils.WebSecurityUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CustomLogoutHandler logoutHandler;

    @Autowired
    public WebSecurityConfig(UserAuthenticationEntryPoint userAuthenticationEntryPoint, JwtAuthenticationProvider jwtAuthenticationProvider, CustomLogoutHandler logoutHandler) {
        this.userAuthenticationEntryPoint = userAuthenticationEntryPoint;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.logoutHandler = logoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(userAuthenticationEntryPoint)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(WebSecurityUtils.publicMappings).permitAll()
                        .requestMatchers(WebSecurityUtils.publicMappingsGET).permitAll()
                        .requestMatchers(WebSecurityUtils.publicMappingsPOST).permitAll()
                        .anyRequest().authenticated()
                )
                .logout((logout) -> logout
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            response.setHeader("message", "You have successfully logged out.");
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.getWriter().flush();
                        }))
                )
                .addFilterAfter(new UsernamePasswordAuthFilter(jwtAuthenticationProvider), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtAuthFilter(jwtAuthenticationProvider), UsernamePasswordAuthFilter.class)
                .addFilterAfter(new CookieTokenAuthenticationFilter(jwtAuthenticationProvider), JwtAuthFilter.class);

        return http.build();
    }

}