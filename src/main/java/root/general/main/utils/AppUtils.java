package root.general.main.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public final class AppUtils {

    public static final String hostUrl = "http://localhost:8080";

    public static String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(header != null) {
            String[] authElements = header.split(" ");
            if (authElements.length == 2 && "Bearer".equals(authElements[0])) {
                return authElements[1];
            }
        }
        else {
            return request.getParameter("token");
        }
        return null;
    }

    public static String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static String formatLocalDateTime(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");
        return date.format(formatter);
    }

}
