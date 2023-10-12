package root.general.security.general.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Component
public class UserAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        //if (WebSecurityUtils.isPublicRequest(request)) return;

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        MAPPER.writeValue(response.getWriter(), "Unauthorized Path");
        response.getWriter().flush();
    }
}
