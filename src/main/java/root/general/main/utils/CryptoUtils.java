package root.general.main.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@UtilityClass
public class CryptoUtils {

    public static String encryptPlainText(String plainText) {
        byte[] encodedBytes = Base64.getEncoder().encode(plainText.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

    public static String decryptPlainText(String encodedText) {
        byte[] encodedBytes = Base64.getDecoder().decode(encodedText.getBytes(StandardCharsets.UTF_8));
        return new String(encodedBytes, StandardCharsets.UTF_8);
    }

}
