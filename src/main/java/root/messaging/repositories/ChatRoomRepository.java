package root.messaging.repositories;

import org.springframework.data.repository.CrudRepository;
import root.messaging.data.ChatRoom;

import java.util.Optional;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Long> {
    Optional<ChatRoom> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
}
