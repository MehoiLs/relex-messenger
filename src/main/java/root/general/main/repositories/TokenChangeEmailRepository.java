package root.general.main.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.main.data.User;
import root.general.main.data.TokenChangeEmail;

import java.util.Optional;

public interface TokenChangeEmailRepository extends CrudRepository<TokenChangeEmail, String> {
    Optional<TokenChangeEmail> findByUser(User user);
    boolean existsByUser(User user);
}
