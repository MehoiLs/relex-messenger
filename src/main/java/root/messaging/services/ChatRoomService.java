package root.messaging.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import root.messaging.data.ChatRoom;
import root.messaging.repositories.ChatRoomRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public ChatRoom save(ChatRoom chatRoom) {
        return chatRoomRepository.save(chatRoom);
    }

    public Optional<String> getChatId(
            Long senderId, Long recipientId, boolean createIfNotExist) {

        List<Long> sortedIds = Arrays.asList(senderId, recipientId);
        Collections.sort(sortedIds);

        var chatId = String.format("%s_%s", sortedIds.get(0), sortedIds.get(1));

        Optional<ChatRoom> existingChat = chatRoomRepository.findBySenderIdAndRecipientId(sortedIds.get(0), sortedIds.get(1));

        return existingChat.map(ChatRoom::getChatId)
                .or(() -> {
                    if (!createIfNotExist) {
                        return Optional.empty();
                    }

                    ChatRoom senderRecipient = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(sortedIds.get(0))
                            .recipientId(sortedIds.get(1))
                            .build();

                    ChatRoom recipientSender = ChatRoom
                            .builder()
                            .chatId(chatId)
                            .senderId(sortedIds.get(1))
                            .recipientId(sortedIds.get(0))
                            .build();

                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);
                });
    }

}
