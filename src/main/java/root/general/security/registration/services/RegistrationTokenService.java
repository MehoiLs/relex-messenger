package root.general.security.registration.services;

import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.general.security.registration.repositories.RegistrationTokensRepository;
import root.general.main.data.User;
import root.general.security.registration.data.RegistrationToken;

import java.time.LocalDateTime;
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

    public RegistrationToken getToken(@NonNull String token) {
        return registrationTokensRepository.findById(token)
                .orElse(null);
    }

    public User getUserByRegistrationToken(@NonNull String token) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findById(token);
        return registrationToken.map(RegistrationToken::getUser).orElse(null);
    }

    public String getRegistrationTokenByUser(@NonNull User user) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.map(RegistrationToken::getToken).orElse(null);
    }

    public boolean tokenExistsForUser(@NonNull User user) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.isPresent();
    }

    public boolean tokenIsExpiredByDate(@NonNull String token, @NonNull LocalDateTime date) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findById(token);
        return registrationToken.map(it -> it.getExpirationDate().isBefore(date))
                .orElse(false);
    }

    public String generateToken(@NonNull User user) {
        return registrationTokensRepository.save(new RegistrationToken(UUID.randomUUID().toString(), user)).getToken();
    }

    public void deleteToken(@NonNull String token) {
        registrationTokensRepository.deleteById(token);
    }

    public void deleteToken(@NonNull RegistrationToken token) {
        registrationTokensRepository.delete(token);
    }
}
