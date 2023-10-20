package com.mehoil.relex.general.security.registration.services;

import com.mehoil.relex.shared.utils.Constants;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.email.EmailService;

import java.util.Locale;

@Service
public class UserConfirmationEmailService {

    private final EmailService emailService;
    private final RegistrationTokenService registrationTokenService;
    private final MessageSource messageSource;

    public UserConfirmationEmailService(EmailService emailService, RegistrationTokenService registrationTokenService, MessageSource messageSource) {
        this.emailService = emailService;
        this.registrationTokenService = registrationTokenService;
        this.messageSource = messageSource;
    }

    public void sendConfirmationEmail(User user) throws DatabaseRecordNotFoundException {
        String token = registrationTokenService.tokenExistsForUser(user)
                ? registrationTokenService.getRegistrationTokenByUser(user)
                : registrationTokenService.generateToken(user);

        String confirmationLink = Constants.HOST_URL + "/register/confirm/" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("[Relex Messenger] Account confirmation");
        mailMessage.setText(
                        messageSource.getMessage("email-default-starter-dear-user", new Object[]{user.getUsername()}, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("email-default-text-registration-confirmation", new Object[]{confirmationLink}, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("email-default-finisher-regards", null, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("relex-messenger", null, Locale.getDefault())
        );

        emailService.sendEmailAsync(mailMessage);
    }
}
