package pl.logic.site.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.Room;
import pl.logic.site.repository.MessageRepository;
import pl.logic.site.repository.RoomRepository;
import pl.logic.site.service.MessageService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final ChatRoomServiceImpl chatRoomService;
    private final RoomRepository roomRepository;

    public Message save(Message Message) {
//        var chatId = chatRoomService
//                .getChatRoomId(Message.getSenderId(), Message.getRecipientId(), true)
//                .orElseThrow(); // You can create your own dedicated exception
//        Message.setChatId(chatId);
        Message.setChatId("5");
        repository.save(Message);
        return Message;
    }

    public List<Message> findMessages(int senderId, int recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    public List<Room> findAllUserRooms(int springUserId) {
        return roomRepository.findAllBySenderId(springUserId);
    }
}
