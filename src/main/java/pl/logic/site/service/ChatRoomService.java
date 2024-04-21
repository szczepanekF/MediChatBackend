package pl.logic.site.service;


import pl.logic.site.model.dao.DoctorDAO;
import pl.logic.site.model.mysql.Doctor;
import pl.logic.site.model.mysql.Room;

import java.util.List;
import java.util.Optional;

/**
 * A service used for creating chat rooms for chatting purposes.
 */
public interface ChatRoomService {
    public Optional<String> getChatRoomId(
            int senderId,
            int recipientId,
            boolean createNewRoomIfNotExists
    );

    public List<Room> createChatId(int senderId, int recipientId);
}
