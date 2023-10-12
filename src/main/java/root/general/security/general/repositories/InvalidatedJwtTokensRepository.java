package root.general.security.general.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.security.general.data.InvalidatedJwtToken;

public interface InvalidatedJwtTokensRepository extends CrudRepository<InvalidatedJwtToken, String> {
}
