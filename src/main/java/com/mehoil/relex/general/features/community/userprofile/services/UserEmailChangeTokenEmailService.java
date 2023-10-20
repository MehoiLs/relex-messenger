package com.mehoil.relex.general.features.community.userprofile.services;

import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.shared.utils.Constants;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.email.EmailService;

import java.util.Locale;

@Service
public class UserEmailChangeTokenEmailService {

    private final EmailService emailService;
    private final UserEmailChangeTokenService userEmailChangeTokenService;
    private final MessageSource messageSource;

    public UserEmailChangeTokenEmailService(EmailService emailService, UserEmailChangeTokenService userEmailChangeTokenService, MessageSource messageSource) {
        this.emailService = emailService;
        this.userEmailChangeTokenService = userEmailChangeTokenService;
        this.messageSource = messageSource;
    }

    public void sendConfirmationEmail(User user, String newEmail) throws DatabaseRecordNotFoundException {
        String token = userEmailChangeTokenService.tokenExistsForUser(user)
                ? userEmailChangeTokenService.getTokenByUser(user).getToken()
                : userEmailChangeTokenService.generateToken(user, newEmail);

        String confirmationLink = Constants.HOST_URL + "/profile/edit/email/confirm/" + token;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newEmail);
        mailMessage.setSubject("[Relex Messenger] E-mail change confirmation");
        mailMessage.setText(
                        messageSource.getMessage("email-default-starter-dear-user", new Object[]{user.getUsername()}, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("email-default-text-email-change-confirmation", new Object[]{confirmationLink}, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("email-default-finisher-regards", null, Locale.getDefault()) + "\n" +
                        messageSource.getMessage("relex-messenger", null, Locale.getDefault())
        );

        emailService.sendEmailAsync(mailMessage);
    }

}
