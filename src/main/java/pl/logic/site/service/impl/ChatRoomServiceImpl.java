package pl.logic.site.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.logic.site.model.mysql.Room;
import pl.logic.site.repository.ChatRoomRepository;
import pl.logic.site.service.ChatRoomService;

import java.util.ArrayList;
import java.util.List;
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
                        List<Room> chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId.getFirst().getChatId());
                    }

                    return  Optional.empty();
                });
    }

    @Override
    public Optional<Room> getChatRoomIdBySenderRecipient(final int senderId, final int recipientId) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
    }

    public List<Room> createChatId(int senderId, int recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        List<Room> rooms = new ArrayList<>();
        Room senderRecipient = Room
                .builder()
                .chatId(chatId)
                .senderId(Integer.parseInt(String.valueOf(senderId)))
                .recipientId(Integer.parseInt(String.valueOf(recipientId)))
                .build();

        senderRecipient = chatRoomRepository.saveAndFlush(senderRecipient);
        rooms.add(senderRecipient);
        Room recipientSender = Room
                .builder()
                .chatId(chatId)
                .senderId(Integer.parseInt(String.valueOf(recipientId)))
                .recipientId(Integer.parseInt(String.valueOf(senderId)))
                .build();

        recipientSender = chatRoomRepository.saveAndFlush(recipientSender);
        rooms.add(recipientSender);

        return rooms;
    }
}
