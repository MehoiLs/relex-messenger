package root.general.security.registration.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.exceptions.DatabaseRecordNotFound;
import root.general.main.exceptions.UserNotFoundException;
import root.general.main.services.user.UserService;
import root.general.main.utils.InfoMessagesUtils;
import root.general.security.general.exceptions.RegistrationException;

import java.util.stream.StreamSupport;

@Slf4j
@Service
public class RegistrationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailConfirmationService emailConfirmationService;
    private final RegistrationTokenService registrationTokenService;
    private final UserService userService;

    public RegistrationService(BCryptPasswordEncoder passwordEncoder, EmailConfirmationService emailConfirmationService, RegistrationTokenService registrationTokenService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.emailConfirmationService = emailConfirmationService;
        this.registrationTokenService = registrationTokenService;
        this.userService = userService;
    }

    public String registerUser(User newUser) throws RegistrationException {
        try {
            User possibleUser = userService.getUserByLogin(newUser.getLogin());
            // Пользователь уже существует и повторно запрашивает письмо подтверждения
            if (!possibleUser.isEnabled()) {
                emailConfirmationService.sendConfirmationEmail(possibleUser);
                log.info("[REGISTRATION SERVICE] A non-enabled user requested a confirmation: " + possibleUser.getLogin());
                return InfoMessagesUtils.requestConfirmationLetterAgainMsg;
            }
            else throw new RegistrationException("Login must be unique.");
        } catch (DatabaseRecordNotFound ignored) {}
        // Новый пользователь
        if (!providedEmailIsUnique(newUser.getEmail()))
            throw new RegistrationException("Email must be unique.");
//        if (!providedLoginIsUnique(newUser.getLogin()))
//            throw new RegistrationException("Login must be unique.");

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

        try {
            emailConfirmationService.sendConfirmationEmail(user);
        } catch (DatabaseRecordNotFound databaseRecordNotFound) {
            log.error("[REGISTRATION SERVICE] Error sending confirmation email for user: {}", user.getLogin(), databaseRecordNotFound);
            throw new RegistrationException("Unexpected error.");
        }
        return InfoMessagesUtils.registrationSuccessConfirmationLetterSentMsg;
    }

    @Transactional
    public boolean confirmAccount(String token) {
        try {
            User user = registrationTokenService.getUserByRegistrationToken(token);
            user.setEnabled(true);
            userService.save(user);
            registrationTokenService.deleteToken(token);
            log.info("[REGISTRATION SERVICE] A new user has enabled their account: " + user.getLogin());
            return true;
        } catch (DatabaseRecordNotFound e) {
            return false;
        }
    }

    private boolean providedLoginIsUnique(String providedLogin) {
        return userService.getAllUsers().stream()
                .noneMatch(user -> user.getLogin().equals(providedLogin));
    }

    private boolean providedEmailIsUnique(String providedEmail) {
        return userService.getAllUsers().stream()
                .noneMatch(user -> user.getEmail().equals(providedEmail));
    }

}
