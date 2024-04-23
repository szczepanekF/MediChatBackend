package pl.logic.site.service;


import pl.logic.site.model.mysql.Message;
import pl.logic.site.model.mysql.Room;

import java.util.List;
import java.util.Optional;

/**
 * A service used for managing messages for chatting purposes.
 */
public interface MessageService {
    public Message save(Message Message);

    public List<Message> findMessages(int senderId, int recipientId);

    public List<Room> findAllUserRooms(int springUserId);
}
