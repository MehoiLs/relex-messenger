package com.mehoil.relex.general.features.community.common.repositories;

import com.mehoil.relex.general.features.community.common.data.FriendRequest;
import com.mehoil.relex.general.user.data.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestsRepository extends JpaRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);
    List<FriendRequest> findByRecipient(User recipient);
    boolean existsBySenderAndRecipient(User sender, User recipient);
    void deleteAllByRecipient(User recipient);
}
