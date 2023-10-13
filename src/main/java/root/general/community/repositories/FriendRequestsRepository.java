package root.general.community.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.community.data.FriendRequest;
import root.general.main.data.User;

import java.util.Optional;

public interface FriendRequestsRepository extends CrudRepository<FriendRequest, Long> {
    Optional<FriendRequest> findBySenderAndRecipient(User sender, User recipient);
    boolean existsBySenderAndRecipient(User sender, User recipient);
    void deleteAllByRecipient(User recipient);
}
