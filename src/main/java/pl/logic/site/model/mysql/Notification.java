package pl.logic.site.model.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Immutable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
@Immutable
@Entity
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;
    @Column(name = "id_sender")
    private int senderId;
    @Column(name = "id_recipient")
    private int recipientId;
    @Column(name = "content")
    private String content;
}
