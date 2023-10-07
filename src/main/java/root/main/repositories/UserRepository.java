package root.main.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import root.main.data.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByLogin(String login);
}
