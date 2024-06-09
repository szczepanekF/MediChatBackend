package pl.logic.site.model.views;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.logic.site.model.mysql.Message;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


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


    public List<Message> getMessagesByDates(Date from, Date to) {
        if (messages == null) {
            return Collections.emptyList();
        }
        return messages.stream()
                .filter(msg -> msg.getTimestamp() != null && !msg.getTimestamp().before(from) && !msg.getTimestamp().after(to))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
}

