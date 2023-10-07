package root.main.repositories;

import org.springframework.data.repository.CrudRepository;
import root.main.data.User;
import root.main.data.dto.TokenChangeEmailDTO;

import java.util.Optional;

public interface TokenChangeEmailRepository extends CrudRepository<TokenChangeEmailDTO, String> {
    Optional<TokenChangeEmailDTO> findByUser(User user);
    boolean existsByUser(User user);
}
