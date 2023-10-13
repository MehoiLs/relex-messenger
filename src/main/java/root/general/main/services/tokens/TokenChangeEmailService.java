package root.general.main.services.tokens;

import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.main.data.User;
import root.general.main.repositories.TokenChangeEmailRepository;
import root.general.main.services.user.UserService;
import root.general.main.data.TokenChangeEmail;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class TokenChangeEmailService {

    private final TokenChangeEmailRepository tokenChangeEmailRepository;
    private final UserService userService;

    @Autowired
    public TokenChangeEmailService(TokenChangeEmailRepository tokenChangeEmailRepository, UserService userService) {
        this.tokenChangeEmailRepository = tokenChangeEmailRepository;
        this.userService = userService;
    }

    public TokenChangeEmail getToken(@NonNull String token) {
        return tokenChangeEmailRepository.findById(token)
                .orElse(null);
    }

    public Set<TokenChangeEmail> getAllTokens() {
        return StreamSupport.stream(tokenChangeEmailRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public String getTokenChangeEmailByUser(@NonNull User user) {
        Optional<TokenChangeEmail> tokenChangeEmail = tokenChangeEmailRepository.findByUser(user);
        return tokenChangeEmail.map(TokenChangeEmail::getToken).orElse(null);
    }

    public String generateToken(@NonNull User user, @NonNull String newEmail) {
        return tokenChangeEmailRepository.save(new TokenChangeEmail(UUID.randomUUID().toString(), newEmail, user)).getToken();
    }

    public boolean userHasExistingToken(@NonNull User user) {
        return tokenChangeEmailRepository.existsByUser(user);
    }

    public boolean tokenIsExpiredByDate(@NonNull String token, @NonNull LocalDateTime date) {
        Optional<TokenChangeEmail> tokenChangeEmail = tokenChangeEmailRepository.findById(token);
        return tokenChangeEmail.map(it -> it.getExpirationDate().isBefore(date))
                .orElse(false);
    }

    @Transactional
    public boolean confirmTokenForUser(@NonNull String token, @NonNull User user) {
        TokenChangeEmail foundToken = getToken(token);
        if (foundToken == null) return false;
        if (userService.getUserById(foundToken.getUser().getId()) == user) return false;
        user.setEmail(foundToken.getNewEmail());
        userService.save(user);
        tokenChangeEmailRepository.delete(foundToken);
        log.info("[TOKEN CHANGE EMAIL SERVICE] User " + user.getLogin() + " has changed their email to: " + user.getEmail());
        return true;
    }

    public boolean tokenExistsForUser(@NonNull User user) {
        Optional<TokenChangeEmail> tokenChangeEmail = tokenChangeEmailRepository.findByUser(user);
        return tokenChangeEmail.isPresent();
    }

    public void deleteToken(TokenChangeEmail token) {
        tokenChangeEmailRepository.delete(token);
    }

}
