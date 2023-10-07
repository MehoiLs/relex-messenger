package root.security.registration.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.security.registration.data.RegistrationToken;
import root.security.registration.repositories.RegistrationTokensRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistrationTokenService {

    private final RegistrationTokensRepository registrationTokensRepository;

    @Autowired
    public RegistrationTokenService(RegistrationTokensRepository registrationTokensRepository) {
        this.registrationTokensRepository = registrationTokensRepository;
    }

    public Set<RegistrationToken> getAllTokens() {
        return StreamSupport.stream(registrationTokensRepository.findAll().spliterator(), false)
                .collect(Collectors.toSet());
    }

    public RegistrationToken getToken(@NotNull String token) {
        return registrationTokensRepository.findById(token)
                .orElse(null);
    }

    public User getUserByRegistrationToken(@NotNull String token) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findById(token);
        return registrationToken.map(RegistrationToken::getUser).orElse(null);
    }

    public String getRegistrationTokenByUser(@NotNull User user) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.map(RegistrationToken::getToken).orElse(null);
    }

    public boolean tokenExistsForUser(@NotNull User user) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.isPresent();
    }

    public boolean tokenIsExpired(@NotNull String token) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findById(token);
        return registrationToken.map(it -> it.getExpirationDate().before(new Date()))
                .orElse(false);
    }

    public boolean tokenIsExpiredByDate(@NotNull String token, @NotNull Date date) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findById(token);
        return registrationToken.map(it -> it.getExpirationDate().before(date))
                .orElse(false);
    }

    public String generateToken(@NotNull User user) {
        return registrationTokensRepository.save(new RegistrationToken(UUID.randomUUID().toString(), user)).getToken();
    }

    public void deleteToken(@NotNull String token) {
        registrationTokensRepository.deleteByToken(token);
    }

    public void deleteToken(@NotNull RegistrationToken token) {
        registrationTokensRepository.delete(token);
    }
}
