package com.mehoil.relex.general.security.registration.services;

import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.security.registration.repositories.RegistrationTokensRepository;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.DatabaseRecordNotFoundException;
import com.mehoil.relex.general.security.registration.data.RegistrationToken;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class RegistrationTokenService {

    private final RegistrationTokensRepository registrationTokensRepository;
    private final MessageSource messageSource;

    public RegistrationTokenService(RegistrationTokensRepository registrationTokensRepository, MessageSource messageSource) {
        this.registrationTokensRepository = registrationTokensRepository;
        this.messageSource = messageSource;
    }

    public Set<RegistrationToken> getAllTokens() {
        return new HashSet<>(registrationTokensRepository.findAll());
    }

    public RegistrationToken getToken(@NonNull String token) throws TokenNotFoundException {
        return registrationTokensRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public User getUserByRegistrationToken(@NonNull String token) throws TokenNotFoundException {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByToken(token);
        return registrationToken.map(RegistrationToken::getUser)
                .orElseThrow(() -> new TokenNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public String getRegistrationTokenByUser(@NonNull User user) throws TokenNotFoundException {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.map(RegistrationToken::getToken)
                .orElseThrow(() -> new TokenNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public boolean tokenExistsForUser(@NonNull User user) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByUser(user);
        return registrationToken.isPresent();
    }

    public boolean tokenIsExpiredByDate(@NonNull String token, @NonNull LocalDateTime date) {
        Optional<RegistrationToken> registrationToken = registrationTokensRepository.findByToken(token);
        return registrationToken.map(it -> it.getExpirationDate().isBefore(date))
                .orElse(false);
    }

    public String generateToken(@NonNull User user) {
        return registrationTokensRepository.save(new RegistrationToken(UUID.randomUUID().toString(), user)).getToken();
    }

    public void deleteToken(@NonNull String token) {
        registrationTokensRepository.deleteByToken(token);
    }

    public void deleteToken(@NonNull RegistrationToken token) {
        registrationTokensRepository.delete(token);
    }
}
