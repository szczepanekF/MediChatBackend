package pl.logic.site.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.logic.site.model.mysql.Room;
import pl.logic.site.repository.ChatRoomRepository;
import pl.logic.site.service.ChatRoomService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public Optional<String> getChatRoomId(
            int senderId,
            int recipientId,
            boolean createNewRoomIfNotExists
    ) {
        return chatRoomRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Room::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    public String createChatId(int senderId, int recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        Room senderRecipient = Room
                .builder()
                .chatId(chatId)
                .senderId(Integer.parseInt(String.valueOf(senderId)))
                .recipientId(Integer.parseInt(String.valueOf(recipientId)))
                .build();

        Room recipientSender = Room
                .builder()
                .chatId(chatId)
                .senderId(Integer.parseInt(String.valueOf(recipientId)))
                .recipientId(Integer.parseInt(String.valueOf(senderId)))
                .build();

        chatRoomRepository.save(senderRecipient);
        chatRoomRepository.save(recipientSender);

        return chatId;
    }
}
