package root.general.main.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class InfoMessagesUtils {

    public static final String registrationSuccessConfirmationLetterSentMsg =
            "A confirmation letter will be sent to your email shortly. " +
            "To complete registration, you will have to activate your account via the sent link. However, " +
            "if you haven't received the letter, try sending the registration request again.";

    public static final String requestConfirmationLetterAgainMsg =
            "You have requested a confirmation letter again. Please check your inbox. " +
            "However, if you haven't received the letter again, try sending the request later.";
    
    public static final String jwtTokenAuthenticationReminderMsg =
            "Remember, the token is given once. If you lost it, you have to logout and then " +
                    "login again to receive a new one.";

    public static final String userRestoredAccountMsg =
            "[YOU HAVE RESTORED YOUR ACCOUNT] ";

    public static final String profileEmailChangeConfirmationLetterSentMsg =
            "A confirmation letter will be sent to the email shortly. " +
            "To complete e-mail change, you will have to set your new e-mail via the sent link. However, " +
            "if you haven't received the letter, try sending the request again.";

    public static final String profileEmailChangeCurrentEmailRequestMsg =
            "New email is the same as current. No changes were made.";

    public static final String invalidImageFormatMsg =
            "Invalid file type. Only JPEG and PNG images are allowed.";

    public static final String fileUploadErrorMsg =
            "Error while uploading the file.";

    public static final String fileNameIsNullMsg =
            fileUploadErrorMsg + " The file name is null.";

    public static final String userRequestedDeleteAccountMsg =
            "You have successfully deleted your account. However, it will remain being deactivated for " +
            "7 days, before it is deleted permanently. In case you want to restore your account, " +
            "you will have to login with your credentials again.";
}
