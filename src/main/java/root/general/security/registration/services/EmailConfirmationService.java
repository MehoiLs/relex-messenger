package root.general.security.registration.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.services.EmailService;
import root.general.main.utils.AppUtils;

@Service
public class EmailConfirmationService {

    private final EmailService emailService;
    private final RegistrationTokenService registrationTokenService;

    public EmailConfirmationService(EmailService emailService, RegistrationTokenService registrationTokenService) {
        this.emailService = emailService;
        this.registrationTokenService = registrationTokenService;
    }

    public void sendConfirmationEmail(User user) throws DatabaseRecordNotFound {
        String token = registrationTokenService.tokenExistsForUser(user)
                ? registrationTokenService.getRegistrationTokenByUser(user)
                : registrationTokenService.generateToken(user);

        String confirmationLink = AppUtils.hostUrl + "/register/confirm/" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("[Relex Messenger] Account confirmation");
        mailMessage.setText("Dear " + user.getFirstName() + ",\nThank you for registering on our platform.\n" +
                "To complete the registration and activate your account, please click on the following link:\n" +
                confirmationLink + "\nIf you did not register on our platform, please ignore this email.\n\n" +
                "Best regards,\n[Relex Messenger]");

        emailService.sendEmailAsync(mailMessage);
    }
}
