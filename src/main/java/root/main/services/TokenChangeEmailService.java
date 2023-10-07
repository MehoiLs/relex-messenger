package root.main.services;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.main.data.User;
import root.main.data.dto.TokenChangeEmailDTO;
import root.main.repositories.TokenChangeEmailRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class TokenChangeEmailService {

    private final TokenChangeEmailRepository tokenChangeEmailRepository;

    @Autowired
    public TokenChangeEmailService(TokenChangeEmailRepository tokenChangeEmailRepository) {
        this.tokenChangeEmailRepository = tokenChangeEmailRepository;
    }

    public TokenChangeEmailDTO createTokenForUser(@NotNull User user) {
        return tokenChangeEmailRepository.save(new TokenChangeEmailDTO(UUID.randomUUID().toString(), user));
    }

    public boolean confirmTokenForUser(String token, @NotNull User user) {
        Optional<TokenChangeEmailDTO> foundToken = tokenChangeEmailRepository.findById(token);
        return foundToken.map(tokenChangeEmailDTO -> tokenChangeEmailDTO.getUser().equals(user))
                .orElse(false);
    }

}
