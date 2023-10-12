package root.general.community.repositories;

import org.springframework.data.repository.CrudRepository;
import root.general.community.data.dto.FriendRequestDTO;
import root.general.main.data.User;

import java.util.Optional;

public interface FriendRequestsRepository extends CrudRepository<FriendRequestDTO, Long> {
    Optional<FriendRequestDTO> findBySenderAndRecipient(User sender, User recipient);
    boolean existsBySenderAndRecipient(User sender, User recipient);
    void deleteAllByRecipient(User recipient);
}
