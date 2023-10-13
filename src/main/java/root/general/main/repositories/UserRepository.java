package root.general.main.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.main.data.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByLogin(String login);
}
