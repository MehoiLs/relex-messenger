package root.security.general.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import root.security.general.components.CustomLogoutHandler;
import root.security.general.filters.JwtAuthFilter;
import root.security.general.filters.UsernamePasswordAuthFilter;
import root.security.general.components.JwtAuthenticationProvider;

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
                        .requestMatchers(HttpMethod.POST, "/register/**", "/login/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/register/**").permitAll()
                        .requestMatchers("/home", "/account", "/users", "/dm").authenticated()
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
                .addFilterBefore(new JwtAuthFilter(jwtAuthenticationProvider), UsernamePasswordAuthFilter.class);

        return http.build();
    }

}
