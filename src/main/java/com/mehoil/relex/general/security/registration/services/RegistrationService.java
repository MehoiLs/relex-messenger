package com.mehoil.relex.general.security.registration.services;

import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import com.mehoil.relex.general.security.general.exceptions.RegistrationException;

import java.util.Locale;


@Slf4j
@Service
public class RegistrationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserConfirmationEmailService userConfirmationEmailService;
    private final RegistrationTokenService registrationTokenService;
    private final UserService userService;
    private final MessageSource messageSource;

    public RegistrationService(BCryptPasswordEncoder passwordEncoder,
                               UserConfirmationEmailService userConfirmationEmailService,
                               RegistrationTokenService registrationTokenService,
                               UserService userService,
                               MessageSource messageSource) {
        this.passwordEncoder = passwordEncoder;
        this.userConfirmationEmailService = userConfirmationEmailService;
        this.registrationTokenService = registrationTokenService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public String registerUser(User newUser) throws RegistrationException {
        try {
            User possibleUser = userService.getUserByLogin(newUser.getLogin());
            // Пользователь уже существует и повторно запрашивает письмо подтверждения
            if (!possibleUser.isEnabled()) {
                userConfirmationEmailService.sendConfirmationEmail(possibleUser);
                log.info("[REGISTRATION SERVICE] A non-enabled user requested a confirmation: " + possibleUser.getLogin());
                return messageSource.getMessage("confirmation-letter-request-again", null, Locale.getDefault());
            }
            else throw new RegistrationException(
                    messageSource.getMessage("registration-login-unique", null, Locale.getDefault())
            );
        } catch (DatabaseRecordNotFoundException ignored) {}
        // Новый пользователь
        if (!providedEmailIsUnique(newUser.getEmail()))
            throw new RegistrationException(
                    messageSource.getMessage("registration-email-unique", null, Locale.getDefault())
            );

        try {
            User user = new User(
                    newUser.getEmail(),
                    newUser.getLogin(),
                    passwordEncoder.encode(newUser.getPassword()),
                    newUser.getUsername(),
                    newUser.getFirstName(),
                    newUser.getLastName()
            );
            userService.save(user);

            log.info("[REGISTRATION SERVICE] A new user has registered their account: \"" + user.getLogin() +
                    "\" awaiting confirmation.");

            userConfirmationEmailService.sendConfirmationEmail(user);
        } catch (ConstraintViolationException violationException) {
            throw new RegistrationException(
                    messageSource.getMessage("credentials-invalid-email", null, Locale.getDefault())
            );
        } catch (DatabaseRecordNotFoundException databaseRecordNotFoundException) {
            log.error("[REGISTRATION SERVICE] Error sending confirmation email for user: {}", newUser.getLogin(), databaseRecordNotFoundException);
            throw new RegistrationException(
                    messageSource.getMessage("error-unexpected", null, Locale.getDefault())
            );
        }
        return messageSource.getMessage("registration-confirmation-letter-sent", null, Locale.getDefault());
    }

    @Transactional
    public String confirmAccount(String token) throws TokenNotFoundException {
        User user = registrationTokenService.getUserByRegistrationToken(token);
        user.setEnabled(true);
        userService.save(user);
        registrationTokenService.deleteToken(token);
        log.info("[REGISTRATION SERVICE] A new user has enabled their account: " + user.getLogin());

        return messageSource.getMessage("registration-success", null, Locale.getDefault());
    }

    private boolean providedLoginIsUnique(String providedLogin) {
        return userService.getAllUsers().stream() //TODO UserExistsByLogin
                .noneMatch(user -> user.getLogin().equals(providedLogin));
    }

    private boolean providedEmailIsUnique(String providedEmail) {
        return userService.getAllUsers().stream()
                .noneMatch(user -> user.getEmail().equals(providedEmail));
    }

}
