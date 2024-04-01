package pl.logic.site.service;


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
}
