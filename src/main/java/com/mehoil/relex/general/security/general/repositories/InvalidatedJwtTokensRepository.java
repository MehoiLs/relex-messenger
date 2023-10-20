package com.mehoil.relex.general.security.general.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mehoil.relex.general.security.general.data.InvalidatedJwtToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvalidatedJwtTokensRepository extends JpaRepository<InvalidatedJwtToken, String> {
    boolean existsByToken(String token);
    Optional<InvalidatedJwtToken> findByToken(String token);
}
