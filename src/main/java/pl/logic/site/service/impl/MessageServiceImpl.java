package pl.logic.site.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.Notification;
import pl.logic.site.model.mysql.Room;
import pl.logic.site.repository.MessageRepository;
import pl.logic.site.repository.RoomRepository;
import pl.logic.site.service.MessageService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final ChatRoomServiceImpl chatRoomService;
    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public Message save(Message message) {
        var chatId = chatRoomService
                .getChatRoomId(message.getSenderId(), message.getRecipientId(), true)
                .orElseThrow(); // You can create your own dedicated exception
        message.setChatId(chatId);
        repository.save(message);

        return message;
    }

    public void send(Message message) {
        messagingTemplate.convertAndSendToUser(
                String.valueOf(message.getRecipientId()), // Recipient ID
                "/queue/messages", // Destination (queue specific to the recipient)
                new Notification(
                        message.getId(),
                        message.getSenderId(),
                        message.getRecipientId(),
                        message.getContent()
                )
        );
    }

    public List<Message> findMessages(int senderId, int recipientId) {
        var chatId = chatRoomService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    public List<Room> findAllUserRooms(int springUserId) {
        return roomRepository.findAllBySenderId(springUserId);
    }
}
