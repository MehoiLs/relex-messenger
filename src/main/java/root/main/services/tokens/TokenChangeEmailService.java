package root.main.services.tokens;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.data.dto.TokenChangeEmailDTO;
import root.main.repositories.TokenChangeEmailRepository;
import root.main.services.UserService;

import java.util.Date;
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

    public TokenChangeEmailDTO getToken(@NotNull String token) {
        return tokenChangeEmailRepository.findById(token)
                .orElse(null);
    }

    public Set<TokenChangeEmailDTO> getAllTokens() {
        return StreamSupport.stream(tokenChangeEmailRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public String getTokenChangeEmailByUser(@NotNull User user) {
        Optional<TokenChangeEmailDTO> tokenChangeEmail = tokenChangeEmailRepository.findByUser(user);
        return tokenChangeEmail.map(TokenChangeEmailDTO::getToken).orElse(null);
    }

    public String generateToken(@NotNull User user, @NotNull String newEmail) {
        return tokenChangeEmailRepository.save(new TokenChangeEmailDTO(UUID.randomUUID().toString(), newEmail, user)).getToken();
    }

    public boolean userHasExistingToken(@NotNull User user) {
        return tokenChangeEmailRepository.existsByUser(user);
    }

    public boolean tokenIsExpiredByDate(@NotNull String token, @NotNull Date date) {
        Optional<TokenChangeEmailDTO> tokenChangeEmail = tokenChangeEmailRepository.findById(token);
        return tokenChangeEmail.map(it -> it.getExpirationDate().before(date))
                .orElse(false);
    }

    @Transactional
    public boolean confirmTokenForUser(@NotNull String token, @NotNull User user) {
        TokenChangeEmailDTO foundToken = getToken(token);
        if (foundToken == null) return false;
        if (userService.getUserById(foundToken.getUser().getId()) == user) return false;
        user.setEmail(foundToken.getNewEmail());
        userService.save(user);
        tokenChangeEmailRepository.delete(foundToken);
        log.info("[TOKEN CHANGE EMAIL SERVICE] User " + user.getLogin() + " has changed their email to: " + user.getEmail());
        return true;
    }

    public boolean tokenExistsForUser(@NotNull User user) {
        Optional<TokenChangeEmailDTO> tokenChangeEmail = tokenChangeEmailRepository.findByUser(user);
        return tokenChangeEmail.isPresent();
    }

    public void deleteToken(TokenChangeEmailDTO token) {
        tokenChangeEmailRepository.delete(token);
    }

}
