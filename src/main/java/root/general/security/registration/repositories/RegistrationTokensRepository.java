package root.general.security.registration.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.main.data.User;
import root.general.security.registration.data.RegistrationToken;

import java.util.Optional;

public interface RegistrationTokensRepository extends CrudRepository<RegistrationToken, String> {
    Optional<RegistrationToken> findByUser(User user);
}
