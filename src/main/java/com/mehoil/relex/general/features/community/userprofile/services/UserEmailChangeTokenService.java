package com.mehoil.relex.general.features.community.userprofile.services;

import com.mehoil.relex.general.features.community.userprofile.repositories.UserEmailChangeTokenRepository;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.database.exceptions.TokenNotFoundException;
import com.mehoil.relex.general.user.services.UserService;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import com.mehoil.relex.general.features.community.userprofile.data.UserEmailChangeToken;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class UserEmailChangeTokenService {

    private final UserEmailChangeTokenRepository userEmailChangeTokenRepository;
    private final UserService userService;
    private final MessageSource messageSource;

    public UserEmailChangeTokenService(UserEmailChangeTokenRepository userEmailChangeTokenRepository, UserService userService, MessageSource messageSource) {
        this.userEmailChangeTokenRepository = userEmailChangeTokenRepository;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    public UserEmailChangeToken getToken(@NonNull String token) throws TokenNotFoundException {
        return userEmailChangeTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public UserEmailChangeToken getTokenByUser(@NonNull User user) throws TokenNotFoundException {
        return userEmailChangeTokenRepository.findByUser(user)
                .orElseThrow(() -> new TokenNotFoundException(
                        messageSource.getMessage("token-not-found", null, Locale.getDefault())
                ));
    }

    public Set<UserEmailChangeToken> getAllTokens() {
        return new HashSet<>(userEmailChangeTokenRepository.findAll());
    }

    public String getEmailFromTokenByUser(@NonNull User user) throws TokenNotFoundException {
        return getTokenByUser(user).getNewEmail();
    }

    public String generateToken(@NonNull User user, @NonNull String newEmail) {
        return userEmailChangeTokenRepository.save(new UserEmailChangeToken(UUID.randomUUID().toString(), newEmail, user)).getToken();
    }

    public boolean tokenExistsForUser(@NonNull User user) {
        return userEmailChangeTokenRepository.existsByUser(user);
    }

    public boolean tokenIsExpiredByDate(@NonNull String token, @NonNull LocalDateTime date) {
        Optional<UserEmailChangeToken> tokenChangeEmail = userEmailChangeTokenRepository.findByToken(token);
        return tokenChangeEmail.map(it -> it.getExpirationDate().isBefore(date))
                .orElse(false);
    }

    @Transactional
    public String confirmTokenForUser(@NonNull String token, @NonNull User user) throws TokenNotFoundException {
        if (!tokenExistsForUser(user)) throw new TokenNotFoundException(
                messageSource.getMessage("token-not-found", null, Locale.getDefault())
        );
        UserEmailChangeToken foundToken = getToken(token);

        user.setEmail(foundToken.getNewEmail());
        userService.save(user);
        userEmailChangeTokenRepository.delete(foundToken);

        log.info("[TOKEN CHANGE EMAIL SERVICE] User " + user.getLogin() + " has changed their email to: " + user.getEmail());
        return messageSource.getMessage("user-change-email-success", null, Locale.getDefault());
    }

    public void deleteToken(UserEmailChangeToken token) {
        userEmailChangeTokenRepository.delete(token);
    }

}
