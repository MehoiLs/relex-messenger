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
            "/register/**", "/login/**", "/login/chat", "/error",
            "/js/**", "/fonts/**", "/images/**", "/favicon.ico", "/css/**"
    };

    public static final String[] publicMappingsPOST = {
            "/register/**", "/login/**", "/login/chat"
    };

    private static final String[] ignoreTokenRequestsList = {
            "/register", "/login", "/css", "/favicon.ico"
    };

    public static boolean isIgnoreTokenRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return Arrays.stream(ignoreTokenRequestsList).sequential().anyMatch(requestURI::startsWith);
    }

}
