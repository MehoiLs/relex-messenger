package root.general.main.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class ValidationUtils {

    private static final String NAME_REGEX = "^[A-Za-z]+$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";

    public static boolean isValidFirstNameOrLastName(String name) {
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
