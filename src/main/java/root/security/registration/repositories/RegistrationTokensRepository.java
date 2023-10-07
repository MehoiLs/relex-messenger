package root.security.registration.repositories;

import org.springframework.data.repository.CrudRepository;
import root.main.data.User;
import root.security.registration.data.RegistrationToken;

import java.util.Optional;
import java.util.UUID;

public interface RegistrationTokensRepository extends CrudRepository<RegistrationToken, String> {
    Optional<RegistrationToken> findByUser(User user);
    void deleteByToken(String token);
}
