package root.main.repositories;

import org.springframework.data.repository.CrudRepository;
import root.main.data.dto.TokenChangeEmailDTO;

public interface TokenChangeEmailRepository extends CrudRepository<TokenChangeEmailDTO, String> {
}
