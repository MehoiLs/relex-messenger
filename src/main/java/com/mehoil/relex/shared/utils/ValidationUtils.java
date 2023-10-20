package com.mehoil.relex.shared.utils;

import lombok.experimental.UtilityClass;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public final class ValidationUtils {

    private static final String NAME_REGEX = "^[A-Za-z]+$";

    public static boolean isInvalidFirstNameOrLastName(String name) {
        Pattern pattern = Pattern.compile(NAME_REGEX);
        Matcher matcher = pattern.matcher(name);
        return !matcher.matches();
    }

}
