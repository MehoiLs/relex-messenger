package root.general.main.services.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.services.EmailService;
import root.general.main.services.tokens.TokenChangeEmailService;
import root.general.main.utils.AppUtils;

@Service
public class EmailTokenChangeService {

    private final EmailService emailService;
    private final TokenChangeEmailService tokenChangeEmailService;

    public EmailTokenChangeService(EmailService emailService, TokenChangeEmailService tokenChangeEmailService) {
        this.emailService = emailService;
        this.tokenChangeEmailService = tokenChangeEmailService;
    }

    public void sendConfirmationEmail(User user, String newEmail) throws DatabaseRecordNotFound {
        String token = tokenChangeEmailService.tokenExistsForUser(user)
                ? tokenChangeEmailService.getTokenChangeEmailByUser(user)
                : tokenChangeEmailService.generateToken(user, newEmail);

        String confirmationLink = AppUtils.hostUrl + "/profile/edit/email/confirm/" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newEmail);
        mailMessage.setSubject("[Relex Messenger] E-mail change confirmation");
        mailMessage.setText("Dear " + user.getFirstName() + ",\nYou have requested an e-mail change.\n" +
                "To complete the e-mail change, please click on the following link:\n" +
                confirmationLink + "\nNote, that once you click on the link, your account " +
                "will no longer be linked to your previous e-mail.\nIf you did not request it, " +
                "please change your credentials ASAP or contact support.\n\n" +
                "Best regards,\n[Relex Messenger]");

        emailService.sendEmailAsync(mailMessage);
    }

}
