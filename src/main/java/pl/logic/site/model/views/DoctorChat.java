package pl.logic.site.model.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.logic.site.model.mysql.Message;

import java.util.Comparator;
import java.util.List;


@Data
@AllArgsConstructor
public class DoctorChat {
    String chatId;
    int doctorId;
    int patientId;
    int springUserId;
    List<Message> messages;

    Message getFirstMessage(){
        if (messages != null && !messages.isEmpty()) {
            // Sort messages by timestamp
            messages.sort(Comparator.comparing(Message::getTimestamp));
            // Return the oldest message
            return messages.get(0);
        } else {
            return null;
        }
    }
}
