package root.general.security.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;

import java.util.Arrays;

@UtilityClass
public final class WebSecurityUtils {

    public static final String[] publicMappings = {
            "/public"
    };

    public static final String[] publicMappingsGET = {
            "/register/**", "/login/**", "/login/chat", "/error", "/public/validation/**",
            "/js/**", "/fonts/**", "/images/**", "/favicon.ico", "/css/**"
    };

    public static final String[] publicMappingsPOST = {
            "/register/**", "/login/**", "/login/chat"
    };

    public static boolean isPublicRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return Arrays.stream(publicMappingsGET).sequential().anyMatch(requestURI::startsWith);
    }

}
