package pl.logic.site.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.logic.site.model.mysql.Room;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findBySenderIdAndRecipientId(int senderId, int recipientId);
}
