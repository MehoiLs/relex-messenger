package root.security.general.repositories;

import org.springframework.data.repository.CrudRepository;
import root.security.general.data.InvalidatedJwtToken;

public interface InvalidatedJwtTokensRepository extends CrudRepository<InvalidatedJwtToken, String> {
}
