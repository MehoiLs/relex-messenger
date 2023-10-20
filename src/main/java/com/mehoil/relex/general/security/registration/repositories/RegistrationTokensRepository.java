package com.mehoil.relex.general.security.registration.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mehoil.relex.general.user.data.User;
import com.mehoil.relex.general.security.registration.data.RegistrationToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationTokensRepository extends JpaRepository<RegistrationToken, String> {
    Optional<RegistrationToken> findByUser(User user);
    Optional<RegistrationToken> findByToken(String token);
    void deleteByToken(String token);
}
