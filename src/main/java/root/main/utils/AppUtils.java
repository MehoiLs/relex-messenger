package root.main.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import root.messaging.data.ChatMessage;

import java.util.List;

@UtilityClass
public final class AppUtils {

    public static final String hostUrl = "http://localhost:8080";
    public static final String hostEmail = "messenger.application@mail.ru";

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

    public static String buildStringFromMessagesList(List<ChatMessage> chatMessages) {
        if (chatMessages == null) return "";
        StringBuilder output = new StringBuilder();
        chatMessages.forEach(msg ->
                output.append(CryptoUtils.decryptPlainText(msg.getContent())).append("\n")
        );
        return output.toString();
    }

}
