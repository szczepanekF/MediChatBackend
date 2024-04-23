package pl.logic.site.service;


import pl.logic.site.model.mysql.SpringUser;

import java.util.List;
import java.util.Optional;

/**
 * A service used for manipulating users for chatting purposes.
 */
public interface UserService {
    public Optional<String> getChatRoomId(
            int senderId,
            int recipientId,
            boolean createNewRoomIfNotExists
    );

    public String createChatId(int senderId, int recipientId);

    public List<Optional<SpringUser>> getAllUsers(int userFilter);

    public Object findSpringUser(int id, boolean patient);


}
