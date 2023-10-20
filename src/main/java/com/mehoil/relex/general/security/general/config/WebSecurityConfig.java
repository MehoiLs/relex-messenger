package com.mehoil.relex.general.security.general.config;

import com.mehoil.relex.general.security.general.components.CustomLogoutHandler;
import com.mehoil.relex.general.security.general.components.JwtAuthenticationProvider;
import com.mehoil.relex.general.security.general.filters.CookieTokenAuthenticationFilter;
import com.mehoil.relex.general.security.general.filters.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import com.mehoil.relex.general.security.utils.WebSecurityUtils;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final CustomLogoutHandler logoutHandler;

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
                        .requestMatchers(HttpMethod.GET, WebSecurityUtils.publicMappingsGET).permitAll()
                        .requestMatchers(HttpMethod.POST, WebSecurityUtils.publicMappingsPOST).permitAll()
                        .requestMatchers("/admin/**", "/swagger-ui/**").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .logout((logout) -> logout
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((logoutHandler::logoutSuccessHandler))
                )
                .addFilterAfter(new JwtAuthFilter(jwtAuthenticationProvider), BasicAuthenticationFilter.class)
                .addFilterAfter(new CookieTokenAuthenticationFilter(jwtAuthenticationProvider), JwtAuthFilter.class);

        return http.build();
    }


}
