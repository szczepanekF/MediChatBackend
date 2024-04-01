package pl.logic.site.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.Notification;
import pl.logic.site.service.impl.MessageServiceImpl;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServiceImpl MessageService;

    /**
     * Endpoint used for sending messages to a specific user using WebSocket.
     * @param Message
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload Message Message) {
        log.info("Wywoływana metoda /chat dla RecipientID:" + Message.getRecipientId());
        Message savedMsg = MessageService.save(Message);
        messagingTemplate.convertAndSendToUser(
                String.valueOf(Message.getRecipientId()), "/queue/messages",
                new Notification(
                        savedMsg.getId(),
                        savedMsg.getSenderId(),
                        savedMsg.getRecipientId(),
                        savedMsg.getContent()
                )
        );
    }

    /**
     * Endpoint used for finding messages by sender and recipient ID.
     * @param senderId
     * @param recipientId
     * @return List of messages belonging to the specified sender&recipient ID
     */
    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Message>> findMessages(@PathVariable int senderId,
                                                 @PathVariable int recipientId) {
        log.info("Wywoływana metoda /messages/senderid/recipientid");
        return ResponseEntity
                .ok(MessageService.findMessages(senderId, recipientId));
    }
}
