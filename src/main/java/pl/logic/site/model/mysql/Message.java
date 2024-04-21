package pl.logic.site.model.mysql;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Immutable
@Entity
@Table(name = "message")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "id_chat")
    private String chatId;
    @Column(name = "id_sender")
    private int senderId; //userId
    @Column(name = "id_recipient")
    private int recipientId; //userId
    @Column(name = "content")
    private String content;
    @Column(name = "timestamp")
    private Date timestamp;
}
