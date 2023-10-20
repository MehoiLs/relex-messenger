package com.mehoil.relex.general.features.community.userprofile.repositories;

import com.mehoil.relex.general.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mehoil.relex.general.features.community.userprofile.data.UserEmailChangeToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserEmailChangeTokenRepository extends JpaRepository<UserEmailChangeToken, String> {
    Optional<UserEmailChangeToken> findByUser(User user);
    Optional<UserEmailChangeToken> findByToken(String token);
    boolean existsByUser(User user);
}
