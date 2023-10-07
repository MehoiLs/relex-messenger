package root.main.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class MessagesUtils {

    public static final String registrationSuccessConfirmationLetterSentMsg =
            "A confirmation letter will be sent to your email shortly. " +
            "To complete registration, you will have to activate your account via the sent link. However, " +
            "if you haven't received the letter, try sending the registration request again.";

    public static final String requestConfirmationLetterAgain =
            "You have requested a confirmation letter again. Please check your inbox. " +
            "However, if you haven't received the letter again, try sending the request later.";

    public static final String jwtTokenAuthenticationReminderMsg =
            "Remember, the token is given once. If you lost it, you have to logout and then " +
                    "login again to receive a new one.";

    public static final String profileEmailChangeConfirmationLetterSentMsg =
            "A confirmation letter will be sent to the email shortly. " +
            "To complete e-mail change, you will have to set your new e-mail via the sent link. However, " +
            "if you haven't received the letter, try sending the request again.";

}
