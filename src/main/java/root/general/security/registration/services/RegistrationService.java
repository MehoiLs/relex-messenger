package root.general.security.registration.services;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.exceptions.UserNotFoundException;
import root.general.security.general.exceptions.RegistrationException;
import root.general.main.services.user.UserService;
import root.general.main.utils.InfoMessagesUtils;

import java.util.stream.StreamSupport;

@Slf4j
@Service
public class RegistrationService {

    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailConfirmationService emailConfirmationService;
    private final RegistrationTokenService registrationTokenService;
    private final UserService userService;

    @Autowired
    public RegistrationService(BCryptPasswordEncoder passwordEncoder, EmailConfirmationService emailConfirmationService, RegistrationTokenService registrationTokenService, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.emailConfirmationService = emailConfirmationService;
        this.registrationTokenService = registrationTokenService;
        this.userService = userService;
    }

    public String registerUser(User newUser) throws RegistrationException {
        try {
            User possibleUser = userService.getUserByLogin(newUser.getLogin());
            // User exists and requests a letter
            if (!possibleUser.isEnabled()) {
                emailConfirmationService.sendConfirmationEmail(possibleUser);
                log.info("[REGISTRATION SERVICE] A non-enabled user requested a confirmation: " + possibleUser.getLogin());
                return InfoMessagesUtils.requestConfirmationLetterAgainMsg;
            }
        } catch (UserNotFoundException ignored) {}
        // New user
        if (!providedEmailIsUnique(newUser.getEmail()))
            throw new RegistrationException("Email must be unique.");
        if (!providedLoginIsUnique(newUser.getLogin()))
            throw new RegistrationException("Login must be unique.");

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

        emailConfirmationService.sendConfirmationEmail(user);
        return InfoMessagesUtils.registrationSuccessConfirmationLetterSentMsg;
    }

    @Transactional
    public boolean confirmAccount(String token) {
        User user = registrationTokenService.getUserByRegistrationToken(token);
        if (user == null) return false;
        user.setEnabled(true);
        userService.save(user);
        registrationTokenService.deleteToken(token);
        log.info("[REGISTRATION SERVICE] A new user has enabled their account: " + user.getLogin());
        return true;
    }

    private boolean providedLoginIsUnique(String providedLogin) {
        return StreamSupport.stream(userService.getAllUsers().spliterator(), false)
                .noneMatch(user -> user.getLogin().equals(providedLogin));
    }

    private boolean providedEmailIsUnique(String providedEmail) {
        return StreamSupport.stream(userService.getAllUsers().spliterator(), false)
                .noneMatch(user -> user.getEmail().equals(providedEmail));
    }

}
